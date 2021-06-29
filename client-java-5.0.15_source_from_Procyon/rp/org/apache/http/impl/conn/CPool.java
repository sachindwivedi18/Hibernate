// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.pool.PoolEntry;
import rp.org.apache.http.pool.PoolEntryCallback;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.pool.ConnFactory;
import java.util.concurrent.TimeUnit;
import rp.org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.pool.AbstractConnPool;

@Contract(threading = ThreadingBehavior.SAFE)
class CPool extends AbstractConnPool<HttpRoute, ManagedHttpClientConnection, CPoolEntry>
{
    private static final AtomicLong COUNTER;
    private final Log log;
    private final long timeToLive;
    private final TimeUnit timeUnit;
    
    public CPool(final ConnFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final int defaultMaxPerRoute, final int maxTotal, final long timeToLive, final TimeUnit timeUnit) {
        super(connFactory, defaultMaxPerRoute, maxTotal);
        this.log = LogFactory.getLog(CPool.class);
        this.timeToLive = timeToLive;
        this.timeUnit = timeUnit;
    }
    
    @Override
    protected CPoolEntry createEntry(final HttpRoute route, final ManagedHttpClientConnection conn) {
        final String id = Long.toString(CPool.COUNTER.getAndIncrement());
        return new CPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
    }
    
    @Override
    protected boolean validate(final CPoolEntry entry) {
        return !((PoolEntry<T, ManagedHttpClientConnection>)entry).getConnection().isStale();
    }
    
    @Override
    protected void enumAvailable(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumAvailable(callback);
    }
    
    @Override
    protected void enumLeased(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumLeased(callback);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}
