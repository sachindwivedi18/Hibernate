// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V>
{
    private static final long serialVersionUID = 7431625294878419160L;
    
    protected AbstractSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    abstract Set<V> createCollection();
    
    @Override
    Set<V> createUnmodifiableEmptyCollection() {
        return Collections.emptySet();
    }
    
    @Override
     <E> Collection<E> unmodifiableCollectionSubclass(final Collection<E> collection) {
        return (Collection<E>)Collections.unmodifiableSet((Set<?>)(Set)collection);
    }
    
    @Override
    Collection<V> wrapCollection(final K key, final Collection<V> collection) {
        return (Collection<V>)new WrappedSet((K)key, (Set)collection);
    }
    
    @Override
    public Set<V> get(final K key) {
        return (Set<V>)(Set)super.get(key);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set<Map.Entry<K, V>>)(Set)super.entries();
    }
    
    @CanIgnoreReturnValue
    @Override
    public Set<V> removeAll(final Object key) {
        return (Set<V>)(Set)super.removeAll(key);
    }
    
    @CanIgnoreReturnValue
    @Override
    public Set<V> replaceValues(final K key, final Iterable<? extends V> values) {
        return (Set<V>)(Set)super.replaceValues(key, values);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean put(final K key, final V value) {
        return super.put(key, value);
    }
    
    @Override
    public boolean equals(final Object object) {
        return super.equals(object);
    }
}
