// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.function.Consumer;
import java.util.Collection;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Map;
import java.util.Set;
import rp.com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import java.util.Arrays;
import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
class CompactLinkedHashMap<K, V> extends CompactHashMap<K, V>
{
    private static final int ENDPOINT = -2;
    @VisibleForTesting
    transient long[] links;
    private transient int firstEntry;
    private transient int lastEntry;
    private final boolean accessOrder;
    
    public static <K, V> CompactLinkedHashMap<K, V> create() {
        return new CompactLinkedHashMap<K, V>();
    }
    
    public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(final int expectedSize) {
        return new CompactLinkedHashMap<K, V>(expectedSize);
    }
    
    CompactLinkedHashMap() {
        this(3);
    }
    
    CompactLinkedHashMap(final int expectedSize) {
        this(expectedSize, 1.0f, false);
    }
    
    CompactLinkedHashMap(final int expectedSize, final float loadFactor, final boolean accessOrder) {
        super(expectedSize, loadFactor);
        this.accessOrder = accessOrder;
    }
    
    @Override
    void init(final int expectedSize, final float loadFactor) {
        super.init(expectedSize, loadFactor);
        this.firstEntry = -2;
        this.lastEntry = -2;
        Arrays.fill(this.links = new long[expectedSize], -1L);
    }
    
    private int getPredecessor(final int entry) {
        return (int)(this.links[entry] >>> 32);
    }
    
    @Override
    int getSuccessor(final int entry) {
        return (int)this.links[entry];
    }
    
    private void setSuccessor(final int entry, final int succ) {
        final long succMask = 4294967295L;
        this.links[entry] = ((this.links[entry] & ~succMask) | ((long)succ & succMask));
    }
    
    private void setPredecessor(final int entry, final int pred) {
        final long predMask = -4294967296L;
        this.links[entry] = ((this.links[entry] & ~predMask) | (long)pred << 32);
    }
    
    private void setSucceeds(final int pred, final int succ) {
        if (pred == -2) {
            this.firstEntry = succ;
        }
        else {
            this.setSuccessor(pred, succ);
        }
        if (succ == -2) {
            this.lastEntry = pred;
        }
        else {
            this.setPredecessor(succ, pred);
        }
    }
    
    @Override
    void insertEntry(final int entryIndex, final K key, final V value, final int hash) {
        super.insertEntry(entryIndex, key, value, hash);
        this.setSucceeds(this.lastEntry, entryIndex);
        this.setSucceeds(entryIndex, -2);
    }
    
    @Override
    void accessEntry(final int index) {
        if (this.accessOrder) {
            this.setSucceeds(this.getPredecessor(index), this.getSuccessor(index));
            this.setSucceeds(this.lastEntry, index);
            this.setSucceeds(index, -2);
            ++this.modCount;
        }
    }
    
    @Override
    void moveLastEntry(final int dstIndex) {
        final int srcIndex = this.size() - 1;
        this.setSucceeds(this.getPredecessor(dstIndex), this.getSuccessor(dstIndex));
        if (dstIndex < srcIndex) {
            this.setSucceeds(this.getPredecessor(srcIndex), dstIndex);
            this.setSucceeds(dstIndex, this.getSuccessor(srcIndex));
        }
        super.moveLastEntry(dstIndex);
    }
    
    @Override
    void resizeEntries(final int newCapacity) {
        super.resizeEntries(newCapacity);
        this.links = Arrays.copyOf(this.links, newCapacity);
    }
    
    @Override
    int firstEntryIndex() {
        return this.firstEntry;
    }
    
    @Override
    int adjustAfterRemove(final int indexBeforeRemove, final int indexRemoved) {
        return (indexBeforeRemove >= this.size()) ? indexRemoved : indexBeforeRemove;
    }
    
    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        for (int i = this.firstEntry; i != -2; i = this.getSuccessor(i)) {
            action.accept((Object)this.keys[i], (Object)this.values[i]);
        }
    }
    
    @Override
    Set<Map.Entry<K, V>> createEntrySet() {
        class 1EntrySetImpl extends EntrySetView
        {
            @Override
            public Spliterator<Map.Entry<K, V>> spliterator() {
                return Spliterators.spliterator((Collection<? extends Map.Entry<K, V>>)this, 17);
            }
        }
        return (Set<Map.Entry<K, V>>)new 1EntrySetImpl();
    }
    
    @Override
    Set<K> createKeySet() {
        class 1KeySetImpl extends KeySetView
        {
            @Override
            public Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }
            
            @Override
            public <T> T[] toArray(final T[] a) {
                return ObjectArrays.toArrayImpl(this, a);
            }
            
            @Override
            public Spliterator<K> spliterator() {
                return Spliterators.spliterator((Collection<? extends K>)this, 17);
            }
            
            @Override
            public void forEach(final Consumer<? super K> action) {
                Preconditions.checkNotNull(action);
                for (int i = CompactLinkedHashMap.this.firstEntry; i != -2; i = CompactLinkedHashMap.this.getSuccessor(i)) {
                    action.accept((Object)CompactLinkedHashMap.this.keys[i]);
                }
            }
        }
        return (Set<K>)new 1KeySetImpl();
    }
    
    @Override
    Collection<V> createValues() {
        class 1ValuesImpl extends ValuesView
        {
            @Override
            public Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }
            
            @Override
            public <T> T[] toArray(final T[] a) {
                return ObjectArrays.toArrayImpl(this, a);
            }
            
            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliterator((Collection<? extends V>)this, 16);
            }
            
            @Override
            public void forEach(final Consumer<? super V> action) {
                Preconditions.checkNotNull(action);
                for (int i = CompactLinkedHashMap.this.firstEntry; i != -2; i = CompactLinkedHashMap.this.getSuccessor(i)) {
                    action.accept((Object)CompactLinkedHashMap.this.values[i]);
                }
            }
        }
        return (Collection<V>)new 1ValuesImpl();
    }
    
    @Override
    public void clear() {
        super.clear();
        this.firstEntry = -2;
        this.lastEntry = -2;
    }
}
