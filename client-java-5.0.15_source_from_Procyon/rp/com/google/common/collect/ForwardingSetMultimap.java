// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V>
{
    @Override
    protected abstract SetMultimap<K, V> delegate();
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }
    
    @Override
    public Set<V> get(final K key) {
        return this.delegate().get(key);
    }
    
    @CanIgnoreReturnValue
    @Override
    public Set<V> removeAll(final Object key) {
        return this.delegate().removeAll(key);
    }
    
    @CanIgnoreReturnValue
    @Override
    public Set<V> replaceValues(final K key, final Iterable<? extends V> values) {
        return this.delegate().replaceValues(key, values);
    }
}
