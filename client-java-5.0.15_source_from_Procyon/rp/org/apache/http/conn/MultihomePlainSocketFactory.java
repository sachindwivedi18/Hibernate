// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import rp.org.apache.http.util.Asserts;
import java.util.Iterator;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import rp.org.apache.http.params.HttpConnectionParams;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import java.net.InetAddress;
import java.net.Socket;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.scheme.SocketFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class MultihomePlainSocketFactory implements SocketFactory
{
    private static final MultihomePlainSocketFactory DEFAULT_FACTORY;
    
    public static MultihomePlainSocketFactory getSocketFactory() {
        return MultihomePlainSocketFactory.DEFAULT_FACTORY;
    }
    
    private MultihomePlainSocketFactory() {
    }
    
    @Override
    public Socket createSocket() {
        return new Socket();
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress localAddress, final int localPort, final HttpParams params) throws IOException {
        Args.notNull(host, "Target host");
        Args.notNull(params, "HTTP parameters");
        Socket sock = socket;
        if (sock == null) {
            sock = this.createSocket();
        }
        if (localAddress != null || localPort > 0) {
            final InetSocketAddress isa = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
            sock.bind(isa);
        }
        final int timeout = HttpConnectionParams.getConnectionTimeout(params);
        final InetAddress[] inetadrs = InetAddress.getAllByName(host);
        final List<InetAddress> addresses = new ArrayList<InetAddress>(inetadrs.length);
        addresses.addAll(Arrays.asList(inetadrs));
        Collections.shuffle(addresses);
        IOException lastEx = null;
        for (final InetAddress remoteAddress : addresses) {
            try {
                sock.connect(new InetSocketAddress(remoteAddress, port), timeout);
            }
            catch (SocketTimeoutException ex2) {
                throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
            }
            catch (IOException ex) {
                sock = new Socket();
                lastEx = ex;
                continue;
            }
            break;
        }
        if (lastEx != null) {
            throw lastEx;
        }
        return sock;
    }
    
    @Override
    public final boolean isSecure(final Socket sock) throws IllegalArgumentException {
        Args.notNull(sock, "Socket");
        Asserts.check(!sock.isClosed(), "Socket is closed");
        return false;
    }
    
    static {
        DEFAULT_FACTORY = new MultihomePlainSocketFactory();
    }
}
