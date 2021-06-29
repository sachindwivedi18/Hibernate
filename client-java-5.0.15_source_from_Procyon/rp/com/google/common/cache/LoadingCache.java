// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import java.util.concurrent.ConcurrentMap;
import rp.com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.base.Function;

@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V>
{
    V get(final K p0) throws ExecutionException;
    
    V getUnchecked(final K p0);
    
    ImmutableMap<K, V> getAll(final Iterable<? extends K> p0) throws ExecutionException;
    
    @Deprecated
    V apply(final K p0);
    
    void refresh(final K p0);
    
    ConcurrentMap<K, V> asMap();
}
