// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import java.net.InetSocketAddress;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.util.LangUtils;
import rp.org.apache.http.util.Asserts;
import java.util.Date;
import java.io.IOException;
import rp.org.apache.http.HttpClientConnection;
import java.util.concurrent.TimeUnit;
import rp.org.apache.http.conn.ConnectionRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.conn.DnsResolver;
import rp.org.apache.http.conn.SchemePortResolver;
import rp.org.apache.http.config.Lookup;
import rp.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import rp.org.apache.http.conn.socket.PlainConnectionSocketFactory;
import rp.org.apache.http.config.RegistryBuilder;
import rp.org.apache.http.conn.socket.ConnectionSocketFactory;
import rp.org.apache.http.config.Registry;
import java.util.concurrent.atomic.AtomicBoolean;
import rp.org.apache.http.config.ConnectionConfig;
import rp.org.apache.http.config.SocketConfig;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.conn.HttpConnectionFactory;
import rp.org.apache.http.conn.HttpClientConnectionOperator;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Closeable;
import rp.org.apache.http.conn.HttpClientConnectionManager;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicHttpClientConnectionManager implements HttpClientConnectionManager, Closeable
{
    private final Log log;
    private final HttpClientConnectionOperator connectionOperator;
    private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
    private ManagedHttpClientConnection conn;
    private HttpRoute route;
    private Object state;
    private long updated;
    private long expiry;
    private boolean leased;
    private SocketConfig socketConfig;
    private ConnectionConfig connConfig;
    private final AtomicBoolean isShutdown;
    
    private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
        return (Registry<ConnectionSocketFactory>)RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", (PlainConnectionSocketFactory)SSLConnectionSocketFactory.getSocketFactory()).build();
    }
    
    public BasicHttpClientConnectionManager(final Lookup<ConnectionSocketFactory> socketFactoryRegistry, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final SchemePortResolver schemePortResolver, final DnsResolver dnsResolver) {
        this(new DefaultHttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver), connFactory);
    }
    
    public BasicHttpClientConnectionManager(final HttpClientConnectionOperator httpClientConnectionOperator, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
        this.log = LogFactory.getLog(this.getClass());
        this.connectionOperator = Args.notNull(httpClientConnectionOperator, "Connection operator");
        this.connFactory = ((connFactory != null) ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE);
        this.expiry = Long.MAX_VALUE;
        this.socketConfig = SocketConfig.DEFAULT;
        this.connConfig = ConnectionConfig.DEFAULT;
        this.isShutdown = new AtomicBoolean(false);
    }
    
    public BasicHttpClientConnectionManager(final Lookup<ConnectionSocketFactory> socketFactoryRegistry, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
        this(socketFactoryRegistry, connFactory, null, null);
    }
    
    public BasicHttpClientConnectionManager(final Lookup<ConnectionSocketFactory> socketFactoryRegistry) {
        this(socketFactoryRegistry, null, null, null);
    }
    
    public BasicHttpClientConnectionManager() {
        this(getDefaultRegistry(), null, null, null);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public void close() {
        if (this.isShutdown.compareAndSet(false, true)) {
            this.closeConnection();
        }
    }
    
    HttpRoute getRoute() {
        return this.route;
    }
    
    Object getState() {
        return this.state;
    }
    
    public synchronized SocketConfig getSocketConfig() {
        return this.socketConfig;
    }
    
    public synchronized void setSocketConfig(final SocketConfig socketConfig) {
        this.socketConfig = ((socketConfig != null) ? socketConfig : SocketConfig.DEFAULT);
    }
    
    public synchronized ConnectionConfig getConnectionConfig() {
        return this.connConfig;
    }
    
    public synchronized void setConnectionConfig(final ConnectionConfig connConfig) {
        this.connConfig = ((connConfig != null) ? connConfig : ConnectionConfig.DEFAULT);
    }
    
    @Override
    public final ConnectionRequest requestConnection(final HttpRoute route, final Object state) {
        Args.notNull(route, "Route");
        return new ConnectionRequest() {
            @Override
            public boolean cancel() {
                return false;
            }
            
            @Override
            public HttpClientConnection get(final long timeout, final TimeUnit timeUnit) {
                return BasicHttpClientConnectionManager.this.getConnection(route, state);
            }
        };
    }
    
    private synchronized void closeConnection() {
        if (this.conn != null) {
            this.log.debug("Closing connection");
            try {
                this.conn.close();
            }
            catch (IOException iox) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("I/O exception closing connection", iox);
                }
            }
            this.conn = null;
        }
    }
    
    private void checkExpiry() {
        if (this.conn != null && System.currentTimeMillis() >= this.expiry) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection expired @ " + new Date(this.expiry));
            }
            this.closeConnection();
        }
    }
    
    synchronized HttpClientConnection getConnection(final HttpRoute route, final Object state) {
        Asserts.check(!this.isShutdown.get(), "Connection manager has been shut down");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Get connection for route " + route);
        }
        Asserts.check(!this.leased, "Connection is still allocated");
        if (!LangUtils.equals(this.route, route) || !LangUtils.equals(this.state, state)) {
            this.closeConnection();
        }
        this.route = route;
        this.state = state;
        this.checkExpiry();
        if (this.conn == null) {
            this.conn = this.connFactory.create(route, this.connConfig);
        }
        this.conn.setSocketTimeout(this.socketConfig.getSoTimeout());
        this.leased = true;
        return this.conn;
    }
    
    @Override
    public synchronized void releaseConnection(final HttpClientConnection conn, final Object state, final long keepalive, final TimeUnit timeUnit) {
        Args.notNull(conn, "Connection");
        Asserts.check(conn == this.conn, "Connection not obtained from this manager");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Releasing connection " + conn);
        }
        if (this.isShutdown.get()) {
            return;
        }
        try {
            this.updated = System.currentTimeMillis();
            if (!this.conn.isOpen()) {
                this.conn = null;
                this.route = null;
                this.conn = null;
                this.expiry = Long.MAX_VALUE;
            }
            else {
                this.state = state;
                this.conn.setSocketTimeout(0);
                if (this.log.isDebugEnabled()) {
                    String s;
                    if (keepalive > 0L) {
                        s = "for " + keepalive + " " + timeUnit;
                    }
                    else {
                        s = "indefinitely";
                    }
                    this.log.debug("Connection can be kept alive " + s);
                }
                if (keepalive > 0L) {
                    this.expiry = this.updated + timeUnit.toMillis(keepalive);
                }
                else {
                    this.expiry = Long.MAX_VALUE;
                }
            }
        }
        finally {
            this.leased = false;
        }
    }
    
    @Override
    public void connect(final HttpClientConnection conn, final HttpRoute route, final int connectTimeout, final HttpContext context) throws IOException {
        Args.notNull(conn, "Connection");
        Args.notNull(route, "HTTP route");
        Asserts.check(conn == this.conn, "Connection not obtained from this manager");
        HttpHost host;
        if (route.getProxyHost() != null) {
            host = route.getProxyHost();
        }
        else {
            host = route.getTargetHost();
        }
        final InetSocketAddress localAddress = route.getLocalSocketAddress();
        this.connectionOperator.connect(this.conn, host, localAddress, connectTimeout, this.socketConfig, context);
    }
    
    @Override
    public void upgrade(final HttpClientConnection conn, final HttpRoute route, final HttpContext context) throws IOException {
        Args.notNull(conn, "Connection");
        Args.notNull(route, "HTTP route");
        Asserts.check(conn == this.conn, "Connection not obtained from this manager");
        this.connectionOperator.upgrade(this.conn, route.getTargetHost(), context);
    }
    
    @Override
    public void routeComplete(final HttpClientConnection conn, final HttpRoute route, final HttpContext context) throws IOException {
    }
    
    @Override
    public synchronized void closeExpiredConnections() {
        if (this.isShutdown.get()) {
            return;
        }
        if (!this.leased) {
            this.checkExpiry();
        }
    }
    
    @Override
    public synchronized void closeIdleConnections(final long idletime, final TimeUnit timeUnit) {
        Args.notNull(timeUnit, "Time unit");
        if (this.isShutdown.get()) {
            return;
        }
        if (!this.leased) {
            long time = timeUnit.toMillis(idletime);
            if (time < 0L) {
                time = 0L;
            }
            final long deadline = System.currentTimeMillis() - time;
            if (this.updated <= deadline) {
                this.closeConnection();
            }
        }
    }
    
    @Override
    public void shutdown() {
        this.close();
    }
}
