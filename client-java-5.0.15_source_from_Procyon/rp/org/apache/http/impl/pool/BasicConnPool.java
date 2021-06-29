// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.pool;

import rp.org.apache.http.pool.PoolEntry;
import rp.org.apache.http.config.ConnectionConfig;
import rp.org.apache.http.config.SocketConfig;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.pool.ConnFactory;
import java.util.concurrent.atomic.AtomicLong;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpClientConnection;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.pool.AbstractConnPool;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicConnPool extends AbstractConnPool<HttpHost, HttpClientConnection, BasicPoolEntry>
{
    private static final AtomicLong COUNTER;
    
    public BasicConnPool(final ConnFactory<HttpHost, HttpClientConnection> connFactory) {
        super(connFactory, 2, 20);
    }
    
    @Deprecated
    public BasicConnPool(final HttpParams params) {
        super(new BasicConnFactory(params), 2, 20);
    }
    
    public BasicConnPool(final SocketConfig sconfig, final ConnectionConfig cconfig) {
        super(new BasicConnFactory(sconfig, cconfig), 2, 20);
    }
    
    public BasicConnPool() {
        super(new BasicConnFactory(SocketConfig.DEFAULT, ConnectionConfig.DEFAULT), 2, 20);
    }
    
    @Override
    protected BasicPoolEntry createEntry(final HttpHost host, final HttpClientConnection conn) {
        return new BasicPoolEntry(Long.toString(BasicConnPool.COUNTER.getAndIncrement()), host, conn);
    }
    
    @Override
    protected boolean validate(final BasicPoolEntry entry) {
        return !((PoolEntry<T, HttpClientConnection>)entry).getConnection().isStale();
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}
