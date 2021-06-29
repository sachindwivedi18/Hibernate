// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.pool;

public interface PoolEntryCallback<T, C>
{
    void process(final PoolEntry<T, C> p0);
}
