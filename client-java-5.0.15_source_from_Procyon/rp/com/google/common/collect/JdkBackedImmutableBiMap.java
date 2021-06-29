// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.annotations.VisibleForTesting;
import com.google.j2objc.annotations.RetainedWith;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Map;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class JdkBackedImmutableBiMap<K, V> extends ImmutableBiMap<K, V>
{
    private final transient ImmutableList<Map.Entry<K, V>> entries;
    private final Map<K, V> forwardDelegate;
    private final Map<V, K> backwardDelegate;
    @LazyInit
    @RetainedWith
    private transient JdkBackedImmutableBiMap<V, K> inverse;
    
    @VisibleForTesting
    static <K, V> ImmutableBiMap<K, V> create(final int n, final Map.Entry<K, V>[] entryArray) {
        final Map<K, V> forwardDelegate = (Map<K, V>)Maps.newHashMapWithExpectedSize(n);
        final Map<V, K> backwardDelegate = (Map<V, K>)Maps.newHashMapWithExpectedSize(n);
        for (int i = 0; i < n; ++i) {
            final Map.Entry<K, V> e = RegularImmutableMap.makeImmutable(entryArray[i]);
            entryArray[i] = e;
            final V oldValue = forwardDelegate.putIfAbsent(e.getKey(), e.getValue());
            if (oldValue != null) {
                throw ImmutableMap.conflictException("key", e.getKey() + "=" + oldValue, entryArray[i]);
            }
            final K oldKey = backwardDelegate.putIfAbsent(e.getValue(), e.getKey());
            if (oldKey != null) {
                throw ImmutableMap.conflictException("value", oldKey + "=" + e.getValue(), entryArray[i]);
            }
        }
        final ImmutableList<Map.Entry<K, V>> entryList = ImmutableList.asImmutableList(entryArray, n);
        return new JdkBackedImmutableBiMap<K, V>(entryList, forwardDelegate, backwardDelegate);
    }
    
    private JdkBackedImmutableBiMap(final ImmutableList<Map.Entry<K, V>> entries, final Map<K, V> forwardDelegate, final Map<V, K> backwardDelegate) {
        this.entries = entries;
        this.forwardDelegate = forwardDelegate;
        this.backwardDelegate = backwardDelegate;
    }
    
    @Override
    public int size() {
        return this.entries.size();
    }
    
    @Override
    public ImmutableBiMap<V, K> inverse() {
        JdkBackedImmutableBiMap<V, K> result = this.inverse;
        if (result == null) {
            result = (this.inverse = (JdkBackedImmutableBiMap<V, K>)new JdkBackedImmutableBiMap((ImmutableList<Map.Entry<Object, Object>>)new InverseEntries(), (Map<Object, Object>)this.backwardDelegate, (Map<Object, Object>)this.forwardDelegate));
            result.inverse = (JdkBackedImmutableBiMap<V, K>)this;
        }
        return (ImmutableBiMap<V, K>)result;
    }
    
    @Override
    public V get(final Object key) {
        return this.forwardDelegate.get(key);
    }
    
    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return (ImmutableSet<Map.Entry<K, V>>)new ImmutableMapEntrySet.RegularEntrySet((ImmutableMap<Object, Object>)this, (ImmutableList<Map.Entry<Object, Object>>)this.entries);
    }
    
    @Override
    ImmutableSet<K> createKeySet() {
        return (ImmutableSet<K>)new ImmutableMapKeySet((ImmutableMap<Object, Object>)this);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    private final class InverseEntries extends ImmutableList<Map.Entry<V, K>>
    {
        @Override
        public Map.Entry<V, K> get(final int index) {
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)JdkBackedImmutableBiMap.this.entries.get(index);
            return Maps.immutableEntry(entry.getValue(), entry.getKey());
        }
        
        @Override
        boolean isPartialView() {
            return false;
        }
        
        @Override
        public int size() {
            return JdkBackedImmutableBiMap.this.entries.size();
        }
    }
}
