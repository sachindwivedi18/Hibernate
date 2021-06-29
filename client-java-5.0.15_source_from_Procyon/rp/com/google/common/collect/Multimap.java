// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Multimap<K, V>
{
    int size();
    
    boolean isEmpty();
    
    boolean containsKey(@CompatibleWith("K") final Object p0);
    
    boolean containsValue(@CompatibleWith("V") final Object p0);
    
    boolean containsEntry(@CompatibleWith("K") final Object p0, @CompatibleWith("V") final Object p1);
    
    @CanIgnoreReturnValue
    boolean put(final K p0, final V p1);
    
    @CanIgnoreReturnValue
    boolean remove(@CompatibleWith("K") final Object p0, @CompatibleWith("V") final Object p1);
    
    @CanIgnoreReturnValue
    boolean putAll(final K p0, final Iterable<? extends V> p1);
    
    @CanIgnoreReturnValue
    boolean putAll(final Multimap<? extends K, ? extends V> p0);
    
    @CanIgnoreReturnValue
    Collection<V> replaceValues(final K p0, final Iterable<? extends V> p1);
    
    @CanIgnoreReturnValue
    Collection<V> removeAll(@CompatibleWith("K") final Object p0);
    
    void clear();
    
    Collection<V> get(final K p0);
    
    Set<K> keySet();
    
    Multiset<K> keys();
    
    Collection<V> values();
    
    Collection<Map.Entry<K, V>> entries();
    
    default void forEach(final BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        this.entries().forEach(entry -> action.accept(entry.getKey(), (Object)entry.getValue()));
    }
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(final Object p0);
    
    int hashCode();
}
