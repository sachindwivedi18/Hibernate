// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V>
{
    Set<V> get(final K p0);
    
    @CanIgnoreReturnValue
    Set<V> removeAll(final Object p0);
    
    @CanIgnoreReturnValue
    Set<V> replaceValues(final K p0, final Iterable<? extends V> p1);
    
    Set<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(final Object p0);
}
