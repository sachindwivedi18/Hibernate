// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import java.util.Date;
import java.io.IOException;
import rp.org.apache.http.HttpClientConnection;
import java.util.concurrent.TimeUnit;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.pool.PoolEntry;

@Contract(threading = ThreadingBehavior.SAFE)
class CPoolEntry extends PoolEntry<HttpRoute, ManagedHttpClientConnection>
{
    private final Log log;
    private volatile boolean routeComplete;
    
    public CPoolEntry(final Log log, final String id, final HttpRoute route, final ManagedHttpClientConnection conn, final long timeToLive, final TimeUnit timeUnit) {
        super(id, route, conn, timeToLive, timeUnit);
        this.log = log;
    }
    
    public void markRouteComplete() {
        this.routeComplete = true;
    }
    
    public boolean isRouteComplete() {
        return this.routeComplete;
    }
    
    public void closeConnection() throws IOException {
        final HttpClientConnection conn = ((PoolEntry<T, HttpClientConnection>)this).getConnection();
        conn.close();
    }
    
    public void shutdownConnection() throws IOException {
        final HttpClientConnection conn = ((PoolEntry<T, HttpClientConnection>)this).getConnection();
        conn.shutdown();
    }
    
    @Override
    public boolean isExpired(final long now) {
        final boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug("Connection " + this + " expired @ " + new Date(this.getExpiry()));
        }
        return expired;
    }
    
    @Override
    public boolean isClosed() {
        final HttpClientConnection conn = ((PoolEntry<T, HttpClientConnection>)this).getConnection();
        return !conn.isOpen();
    }
    
    @Override
    public void close() {
        try {
            this.closeConnection();
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
}
