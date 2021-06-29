// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.pool;

import java.io.IOException;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpClientConnection;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.pool.PoolEntry;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection>
{
    public BasicPoolEntry(final String id, final HttpHost route, final HttpClientConnection conn) {
        super(id, route, conn);
    }
    
    @Override
    public void close() {
        try {
            ((PoolEntry<T, HttpClientConnection>)this).getConnection().close();
        }
        catch (IOException ex) {}
    }
    
    @Override
    public boolean isClosed() {
        return !((PoolEntry<T, HttpClientConnection>)this).getConnection().isOpen();
    }
}
