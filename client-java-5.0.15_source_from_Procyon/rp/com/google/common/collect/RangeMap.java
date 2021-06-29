// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Map;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public interface RangeMap<K extends Comparable, V>
{
    V get(final K p0);
    
    Map.Entry<Range<K>, V> getEntry(final K p0);
    
    Range<K> span();
    
    void put(final Range<K> p0, final V p1);
    
    void putCoalescing(final Range<K> p0, final V p1);
    
    void putAll(final RangeMap<K, V> p0);
    
    void clear();
    
    void remove(final Range<K> p0);
    
    Map<Range<K>, V> asMapOfRanges();
    
    Map<Range<K>, V> asDescendingMapOfRanges();
    
    RangeMap<K, V> subRangeMap(final Range<K> p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
