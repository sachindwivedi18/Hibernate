// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable
{
    final K key;
    final V value;
    private static final long serialVersionUID = 0L;
    
    ImmutableEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public final K getKey() {
        return this.key;
    }
    
    @Override
    public final V getValue() {
        return this.value;
    }
    
    @Override
    public final V setValue(final V value) {
        throw new UnsupportedOperationException();
    }
}
