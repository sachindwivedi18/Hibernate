// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import rp.com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import com.google.errorprone.annotations.CompatibleWith;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Cache<K, V>
{
    V getIfPresent(@CompatibleWith("K") final Object p0);
    
    V get(final K p0, final Callable<? extends V> p1) throws ExecutionException;
    
    ImmutableMap<K, V> getAllPresent(final Iterable<?> p0);
    
    void put(final K p0, final V p1);
    
    void putAll(final Map<? extends K, ? extends V> p0);
    
    void invalidate(@CompatibleWith("K") final Object p0);
    
    void invalidateAll(final Iterable<?> p0);
    
    void invalidateAll();
    
    long size();
    
    CacheStats stats();
    
    ConcurrentMap<K, V> asMap();
    
    void cleanUp();
}
