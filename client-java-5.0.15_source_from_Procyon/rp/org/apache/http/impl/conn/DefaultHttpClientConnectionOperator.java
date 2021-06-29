// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import rp.org.apache.http.client.protocol.HttpClientContext;
import java.net.Socket;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import rp.org.apache.http.conn.HttpHostConnectException;
import java.net.SocketTimeoutException;
import java.io.IOException;
import rp.org.apache.http.conn.ConnectTimeoutException;
import java.net.InetAddress;
import rp.org.apache.http.conn.UnsupportedSchemeException;
import rp.org.apache.http.config.SocketConfig;
import java.net.InetSocketAddress;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.util.Args;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.conn.DnsResolver;
import rp.org.apache.http.conn.SchemePortResolver;
import rp.org.apache.http.conn.socket.ConnectionSocketFactory;
import rp.org.apache.http.config.Lookup;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.HttpClientConnectionOperator;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpClientConnectionOperator implements HttpClientConnectionOperator
{
    static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
    private final Log log;
    private final Lookup<ConnectionSocketFactory> socketFactoryRegistry;
    private final SchemePortResolver schemePortResolver;
    private final DnsResolver dnsResolver;
    
    public DefaultHttpClientConnectionOperator(final Lookup<ConnectionSocketFactory> socketFactoryRegistry, final SchemePortResolver schemePortResolver, final DnsResolver dnsResolver) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(socketFactoryRegistry, "Socket factory registry");
        this.socketFactoryRegistry = socketFactoryRegistry;
        this.schemePortResolver = ((schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE);
        this.dnsResolver = ((dnsResolver != null) ? dnsResolver : SystemDefaultDnsResolver.INSTANCE);
    }
    
    private Lookup<ConnectionSocketFactory> getSocketFactoryRegistry(final HttpContext context) {
        Lookup<ConnectionSocketFactory> reg = (Lookup<ConnectionSocketFactory>)context.getAttribute("http.socket-factory-registry");
        if (reg == null) {
            reg = this.socketFactoryRegistry;
        }
        return reg;
    }
    
    @Override
    public void connect(final ManagedHttpClientConnection conn, final HttpHost host, final InetSocketAddress localAddress, final int connectTimeout, final SocketConfig socketConfig, final HttpContext context) throws IOException {
        final Lookup<ConnectionSocketFactory> registry = this.getSocketFactoryRegistry(context);
        final ConnectionSocketFactory sf = registry.lookup(host.getSchemeName());
        if (sf == null) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
        }
        final InetAddress[] addresses = (host.getAddress() != null) ? new InetAddress[] { host.getAddress() } : this.dnsResolver.resolve(host.getHostName());
        final int port = this.schemePortResolver.resolve(host);
        for (int i = 0; i < addresses.length; ++i) {
            final InetAddress address = addresses[i];
            final boolean last = i == addresses.length - 1;
            Socket sock = sf.createSocket(context);
            sock.setSoTimeout(socketConfig.getSoTimeout());
            sock.setReuseAddress(socketConfig.isSoReuseAddress());
            sock.setTcpNoDelay(socketConfig.isTcpNoDelay());
            sock.setKeepAlive(socketConfig.isSoKeepAlive());
            if (socketConfig.getRcvBufSize() > 0) {
                sock.setReceiveBufferSize(socketConfig.getRcvBufSize());
            }
            if (socketConfig.getSndBufSize() > 0) {
                sock.setSendBufferSize(socketConfig.getSndBufSize());
            }
            final int linger = socketConfig.getSoLinger();
            if (linger >= 0) {
                sock.setSoLinger(true, linger);
            }
            conn.bind(sock);
            final InetSocketAddress remoteAddress = new InetSocketAddress(address, port);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connecting to " + remoteAddress);
            }
            try {
                sock = sf.connectSocket(connectTimeout, sock, host, remoteAddress, localAddress, context);
                conn.bind(sock);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Connection established " + conn);
                }
                return;
            }
            catch (SocketTimeoutException ex) {
                if (last) {
                    throw new ConnectTimeoutException(ex, host, addresses);
                }
            }
            catch (ConnectException ex2) {
                if (last) {
                    final String msg = ex2.getMessage();
                    throw "Connection timed out".equals(msg) ? new ConnectTimeoutException(ex2, host, addresses) : new HttpHostConnectException(ex2, host, addresses);
                }
            }
            catch (NoRouteToHostException ex3) {
                if (last) {
                    throw ex3;
                }
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connect to " + remoteAddress + " timed out. " + "Connection will be retried using another IP address");
            }
        }
    }
    
    @Override
    public void upgrade(final ManagedHttpClientConnection conn, final HttpHost host, final HttpContext context) throws IOException {
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final Lookup<ConnectionSocketFactory> registry = this.getSocketFactoryRegistry(clientContext);
        final ConnectionSocketFactory sf = registry.lookup(host.getSchemeName());
        if (sf == null) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
        }
        if (!(sf instanceof LayeredConnectionSocketFactory)) {
            throw new UnsupportedSchemeException(host.getSchemeName() + " protocol does not support connection upgrade");
        }
        final LayeredConnectionSocketFactory lsf = (LayeredConnectionSocketFactory)sf;
        Socket sock = conn.getSocket();
        final int port = this.schemePortResolver.resolve(host);
        sock = lsf.createLayeredSocket(sock, host.getHostName(), port, context);
        conn.bind(sock);
    }
}
