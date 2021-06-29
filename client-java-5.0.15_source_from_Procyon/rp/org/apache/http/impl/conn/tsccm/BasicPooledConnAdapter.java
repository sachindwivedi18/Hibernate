// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn.tsccm;

import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.impl.conn.AbstractPoolEntry;
import rp.org.apache.http.impl.conn.AbstractPooledConnAdapter;

@Deprecated
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter
{
    protected BasicPooledConnAdapter(final ThreadSafeClientConnManager tsccm, final AbstractPoolEntry entry) {
        super(tsccm, entry);
        this.markReusable();
    }
    
    @Override
    protected ClientConnectionManager getManager() {
        return super.getManager();
    }
    
    @Override
    protected AbstractPoolEntry getPoolEntry() {
        return super.getPoolEntry();
    }
    
    @Override
    protected void detach() {
        super.detach();
    }
}
