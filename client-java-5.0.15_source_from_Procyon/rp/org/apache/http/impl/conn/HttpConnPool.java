// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import java.io.IOException;
import rp.org.apache.http.pool.PoolEntry;
import rp.org.apache.http.pool.ConnFactory;
import rp.org.apache.http.conn.ClientConnectionOperator;
import java.util.concurrent.TimeUnit;
import rp.org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import rp.org.apache.http.conn.OperatedClientConnection;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.pool.AbstractConnPool;

@Deprecated
class HttpConnPool extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry>
{
    private static final AtomicLong COUNTER;
    private final Log log;
    private final long timeToLive;
    private final TimeUnit timeUnit;
    
    public HttpConnPool(final Log log, final ClientConnectionOperator connOperator, final int defaultMaxPerRoute, final int maxTotal, final long timeToLive, final TimeUnit timeUnit) {
        super(new InternalConnFactory(connOperator), defaultMaxPerRoute, maxTotal);
        this.log = log;
        this.timeToLive = timeToLive;
        this.timeUnit = timeUnit;
    }
    
    @Override
    protected HttpPoolEntry createEntry(final HttpRoute route, final OperatedClientConnection conn) {
        final String id = Long.toString(HttpConnPool.COUNTER.getAndIncrement());
        return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
    
    static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection>
    {
        private final ClientConnectionOperator connOperator;
        
        InternalConnFactory(final ClientConnectionOperator connOperator) {
            this.connOperator = connOperator;
        }
        
        @Override
        public OperatedClientConnection create(final HttpRoute route) throws IOException {
            return this.connOperator.createConnection();
        }
    }
}
