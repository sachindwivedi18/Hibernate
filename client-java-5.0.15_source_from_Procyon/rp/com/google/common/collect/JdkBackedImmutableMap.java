// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import java.util.Map;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class JdkBackedImmutableMap<K, V> extends ImmutableMap<K, V>
{
    private final transient Map<K, V> delegateMap;
    private final transient ImmutableList<Map.Entry<K, V>> entries;
    
    static <K, V> ImmutableMap<K, V> create(final int n, final Map.Entry<K, V>[] entryArray) {
        final Map<K, V> delegateMap = (Map<K, V>)Maps.newHashMapWithExpectedSize(n);
        for (int i = 0; i < n; ++i) {
            entryArray[i] = RegularImmutableMap.makeImmutable(entryArray[i]);
            final V oldValue = delegateMap.putIfAbsent(entryArray[i].getKey(), entryArray[i].getValue());
            if (oldValue != null) {
                throw ImmutableMap.conflictException("key", entryArray[i], entryArray[i].getKey() + "=" + oldValue);
            }
        }
        return new JdkBackedImmutableMap<K, V>(delegateMap, ImmutableList.asImmutableList(entryArray, n));
    }
    
    JdkBackedImmutableMap(final Map<K, V> delegateMap, final ImmutableList<Map.Entry<K, V>> entries) {
        this.delegateMap = delegateMap;
        this.entries = entries;
    }
    
    @Override
    public int size() {
        return this.entries.size();
    }
    
    @Override
    public V get(final Object key) {
        return this.delegateMap.get(key);
    }
    
    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return (ImmutableSet<Map.Entry<K, V>>)new ImmutableMapEntrySet.RegularEntrySet((ImmutableMap<Object, Object>)this, (ImmutableList<Map.Entry<Object, Object>>)this.entries);
    }
    
    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        this.entries.forEach(e -> action.accept(e.getKey(), (Object)e.getValue()));
    }
    
    @Override
    ImmutableSet<K> createKeySet() {
        return (ImmutableSet<K>)new ImmutableMapKeySet((ImmutableMap<Object, Object>)this);
    }
    
    @Override
    ImmutableCollection<V> createValues() {
        return (ImmutableCollection<V>)new ImmutableMapValues((ImmutableMap<Object, Object>)this);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
}
