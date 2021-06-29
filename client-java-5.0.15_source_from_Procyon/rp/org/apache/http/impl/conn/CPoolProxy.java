// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.pool.PoolEntry;
import java.net.InetAddress;
import rp.org.apache.http.HttpConnectionMetrics;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpRequest;
import javax.net.ssl.SSLSession;
import java.net.Socket;
import rp.org.apache.http.HttpClientConnection;
import java.io.IOException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.conn.ManagedHttpClientConnection;

class CPoolProxy implements ManagedHttpClientConnection, HttpContext
{
    private volatile CPoolEntry poolEntry;
    
    CPoolProxy(final CPoolEntry entry) {
        this.poolEntry = entry;
    }
    
    CPoolEntry getPoolEntry() {
        return this.poolEntry;
    }
    
    CPoolEntry detach() {
        final CPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }
    
    ManagedHttpClientConnection getConnection() {
        final CPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return ((PoolEntry<T, ManagedHttpClientConnection>)local).getConnection();
    }
    
    ManagedHttpClientConnection getValidConnection() {
        final ManagedHttpClientConnection conn = this.getConnection();
        if (conn == null) {
            throw new ConnectionShutdownException();
        }
        return conn;
    }
    
    @Override
    public void close() throws IOException {
        final CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.closeConnection();
        }
    }
    
    @Override
    public void shutdown() throws IOException {
        final CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.shutdownConnection();
        }
    }
    
    @Override
    public boolean isOpen() {
        final CPoolEntry local = this.poolEntry;
        return local != null && !local.isClosed();
    }
    
    @Override
    public boolean isStale() {
        final HttpClientConnection conn = this.getConnection();
        return conn == null || conn.isStale();
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        this.getValidConnection().setSocketTimeout(timeout);
    }
    
    @Override
    public int getSocketTimeout() {
        return this.getValidConnection().getSocketTimeout();
    }
    
    @Override
    public String getId() {
        return this.getValidConnection().getId();
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        this.getValidConnection().bind(socket);
    }
    
    @Override
    public Socket getSocket() {
        return this.getValidConnection().getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        return this.getValidConnection().getSSLSession();
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        return this.getValidConnection().isResponseAvailable(timeout);
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        this.getValidConnection().sendRequestHeader(request);
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        this.getValidConnection().sendRequestEntity(request);
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        return this.getValidConnection().receiveResponseHeader();
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        this.getValidConnection().receiveResponseEntity(response);
    }
    
    @Override
    public void flush() throws IOException {
        this.getValidConnection().flush();
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.getValidConnection().getMetrics();
    }
    
    @Override
    public InetAddress getLocalAddress() {
        return this.getValidConnection().getLocalAddress();
    }
    
    @Override
    public int getLocalPort() {
        return this.getValidConnection().getLocalPort();
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        return this.getValidConnection().getRemoteAddress();
    }
    
    @Override
    public int getRemotePort() {
        return this.getValidConnection().getRemotePort();
    }
    
    @Override
    public Object getAttribute(final String id) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        return (conn instanceof HttpContext) ? ((HttpContext)conn).getAttribute(id) : null;
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        if (conn instanceof HttpContext) {
            ((HttpContext)conn).setAttribute(id, obj);
        }
    }
    
    @Override
    public Object removeAttribute(final String id) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        return (conn instanceof HttpContext) ? ((HttpContext)conn).removeAttribute(id) : null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CPoolProxy{");
        final ManagedHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            sb.append(conn);
        }
        else {
            sb.append("detached");
        }
        sb.append('}');
        return sb.toString();
    }
    
    public static HttpClientConnection newProxy(final CPoolEntry poolEntry) {
        return new CPoolProxy(poolEntry);
    }
    
    private static CPoolProxy getProxy(final HttpClientConnection conn) {
        if (!CPoolProxy.class.isInstance(conn)) {
            throw new IllegalStateException("Unexpected connection proxy class: " + conn.getClass());
        }
        return CPoolProxy.class.cast(conn);
    }
    
    public static CPoolEntry getPoolEntry(final HttpClientConnection proxy) {
        final CPoolEntry entry = getProxy(proxy).getPoolEntry();
        if (entry == null) {
            throw new ConnectionShutdownException();
        }
        return entry;
    }
    
    public static CPoolEntry detach(final HttpClientConnection conn) {
        return getProxy(conn).detach();
    }
}
