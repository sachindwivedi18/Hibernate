// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V>
{
    protected ForwardingListMultimap() {
    }
    
    @Override
    protected abstract ListMultimap<K, V> delegate();
    
    @Override
    public List<V> get(final K key) {
        return this.delegate().get(key);
    }
    
    @CanIgnoreReturnValue
    @Override
    public List<V> removeAll(final Object key) {
        return this.delegate().removeAll(key);
    }
    
    @CanIgnoreReturnValue
    @Override
    public List<V> replaceValues(final K key, final Iterable<? extends V> values) {
        return this.delegate().replaceValues(key, values);
    }
}
