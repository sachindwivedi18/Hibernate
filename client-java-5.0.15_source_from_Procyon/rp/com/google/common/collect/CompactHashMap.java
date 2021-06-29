// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.function.Consumer;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import java.util.Iterator;
import java.util.function.BiFunction;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.base.Objects;
import java.util.Arrays;
import rp.com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.AbstractMap;

@GwtIncompatible
class CompactHashMap<K, V> extends AbstractMap<K, V> implements Serializable
{
    private static final int MAXIMUM_CAPACITY = 1073741824;
    static final float DEFAULT_LOAD_FACTOR = 1.0f;
    private static final long NEXT_MASK = 4294967295L;
    private static final long HASH_MASK = -4294967296L;
    static final int DEFAULT_SIZE = 3;
    static final int UNSET = -1;
    private transient int[] table;
    @VisibleForTesting
    transient long[] entries;
    @VisibleForTesting
    transient Object[] keys;
    @VisibleForTesting
    transient Object[] values;
    transient float loadFactor;
    transient int modCount;
    private transient int threshold;
    private transient int size;
    private transient Set<K> keySetView;
    private transient Set<Map.Entry<K, V>> entrySetView;
    private transient Collection<V> valuesView;
    
    public static <K, V> CompactHashMap<K, V> create() {
        return new CompactHashMap<K, V>();
    }
    
    public static <K, V> CompactHashMap<K, V> createWithExpectedSize(final int expectedSize) {
        return new CompactHashMap<K, V>(expectedSize);
    }
    
    CompactHashMap() {
        this.init(3, 1.0f);
    }
    
    CompactHashMap(final int capacity) {
        this(capacity, 1.0f);
    }
    
    CompactHashMap(final int expectedSize, final float loadFactor) {
        this.init(expectedSize, loadFactor);
    }
    
    void init(final int expectedSize, final float loadFactor) {
        Preconditions.checkArgument(expectedSize >= 0, (Object)"Initial capacity must be non-negative");
        Preconditions.checkArgument(loadFactor > 0.0f, (Object)"Illegal load factor");
        final int buckets = Hashing.closedTableSize(expectedSize, loadFactor);
        this.table = newTable(buckets);
        this.loadFactor = loadFactor;
        this.keys = new Object[expectedSize];
        this.values = new Object[expectedSize];
        this.entries = newEntries(expectedSize);
        this.threshold = Math.max(1, (int)(buckets * loadFactor));
    }
    
    private static int[] newTable(final int size) {
        final int[] array = new int[size];
        Arrays.fill(array, -1);
        return array;
    }
    
    private static long[] newEntries(final int size) {
        final long[] array = new long[size];
        Arrays.fill(array, -1L);
        return array;
    }
    
    private int hashTableMask() {
        return this.table.length - 1;
    }
    
    private static int getHash(final long entry) {
        return (int)(entry >>> 32);
    }
    
    private static int getNext(final long entry) {
        return (int)entry;
    }
    
    private static long swapNext(final long entry, final int newNext) {
        return (0xFFFFFFFF00000000L & entry) | (0xFFFFFFFFL & (long)newNext);
    }
    
    void accessEntry(final int index) {
    }
    
    @CanIgnoreReturnValue
    @Override
    public V put(final K key, final V value) {
        final long[] entries = this.entries;
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        final int hash = Hashing.smearedHash(key);
        final int tableIndex = hash & this.hashTableMask();
        final int newEntryIndex = this.size;
        int next = this.table[tableIndex];
        if (next == -1) {
            this.table[tableIndex] = newEntryIndex;
        }
        else {
            int last;
            long entry;
            do {
                last = next;
                entry = entries[next];
                if (getHash(entry) == hash && Objects.equal(key, keys[next])) {
                    final V oldValue = (V)values[next];
                    values[next] = value;
                    this.accessEntry(next);
                    return oldValue;
                }
                next = getNext(entry);
            } while (next != -1);
            entries[last] = swapNext(entry, newEntryIndex);
        }
        if (newEntryIndex == Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot contain more than Integer.MAX_VALUE elements!");
        }
        final int newSize = newEntryIndex + 1;
        this.resizeMeMaybe(newSize);
        this.insertEntry(newEntryIndex, key, value, hash);
        this.size = newSize;
        if (newEntryIndex >= this.threshold) {
            this.resizeTable(2 * this.table.length);
        }
        ++this.modCount;
        return null;
    }
    
    void insertEntry(final int entryIndex, final K key, final V value, final int hash) {
        this.entries[entryIndex] = ((long)hash << 32 | 0xFFFFFFFFL);
        this.keys[entryIndex] = key;
        this.values[entryIndex] = value;
    }
    
    private void resizeMeMaybe(final int newSize) {
        final int entriesSize = this.entries.length;
        if (newSize > entriesSize) {
            int newCapacity = entriesSize + Math.max(1, entriesSize >>> 1);
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            }
            if (newCapacity != entriesSize) {
                this.resizeEntries(newCapacity);
            }
        }
    }
    
    void resizeEntries(final int newCapacity) {
        this.keys = Arrays.copyOf(this.keys, newCapacity);
        this.values = Arrays.copyOf(this.values, newCapacity);
        long[] entries = this.entries;
        final int oldCapacity = entries.length;
        entries = Arrays.copyOf(entries, newCapacity);
        if (newCapacity > oldCapacity) {
            Arrays.fill(entries, oldCapacity, newCapacity, -1L);
        }
        this.entries = entries;
    }
    
    private void resizeTable(final int newCapacity) {
        final int[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity >= 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final int newThreshold = 1 + (int)(newCapacity * this.loadFactor);
        final int[] newTable = newTable(newCapacity);
        final long[] entries = this.entries;
        final int mask = newTable.length - 1;
        for (int i = 0; i < this.size; ++i) {
            final long oldEntry = entries[i];
            final int hash = getHash(oldEntry);
            final int tableIndex = hash & mask;
            final int next = newTable[tableIndex];
            entries[newTable[tableIndex] = i] = ((long)hash << 32 | (0xFFFFFFFFL & (long)next));
        }
        this.threshold = newThreshold;
        this.table = newTable;
    }
    
    private int indexOf(final Object key) {
        final int hash = Hashing.smearedHash(key);
        long entry;
        for (int next = this.table[hash & this.hashTableMask()]; next != -1; next = getNext(entry)) {
            entry = this.entries[next];
            if (getHash(entry) == hash && Objects.equal(key, this.keys[next])) {
                return next;
            }
        }
        return -1;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.indexOf(key) != -1;
    }
    
    @Override
    public V get(final Object key) {
        final int index = this.indexOf(key);
        this.accessEntry(index);
        return (V)((index == -1) ? null : this.values[index]);
    }
    
    @CanIgnoreReturnValue
    @Override
    public V remove(final Object key) {
        return this.remove(key, Hashing.smearedHash(key));
    }
    
    private V remove(final Object key, final int hash) {
        final int tableIndex = hash & this.hashTableMask();
        int next = this.table[tableIndex];
        if (next == -1) {
            return null;
        }
        int last = -1;
        while (getHash(this.entries[next]) != hash || !Objects.equal(key, this.keys[next])) {
            last = next;
            next = getNext(this.entries[next]);
            if (next == -1) {
                return null;
            }
        }
        final V oldValue = (V)this.values[next];
        if (last == -1) {
            this.table[tableIndex] = getNext(this.entries[next]);
        }
        else {
            this.entries[last] = swapNext(this.entries[last], getNext(this.entries[next]));
        }
        this.moveLastEntry(next);
        --this.size;
        ++this.modCount;
        return oldValue;
    }
    
    @CanIgnoreReturnValue
    private V removeEntry(final int entryIndex) {
        return this.remove(this.keys[entryIndex], getHash(this.entries[entryIndex]));
    }
    
    void moveLastEntry(final int dstIndex) {
        final int srcIndex = this.size() - 1;
        if (dstIndex < srcIndex) {
            this.keys[dstIndex] = this.keys[srcIndex];
            this.values[dstIndex] = this.values[srcIndex];
            this.keys[srcIndex] = null;
            this.values[srcIndex] = null;
            final long lastEntry = this.entries[srcIndex];
            this.entries[dstIndex] = lastEntry;
            this.entries[srcIndex] = -1L;
            final int tableIndex = getHash(lastEntry) & this.hashTableMask();
            int lastNext = this.table[tableIndex];
            if (lastNext == srcIndex) {
                this.table[tableIndex] = dstIndex;
            }
            else {
                int previous;
                long entry;
                do {
                    previous = lastNext;
                    lastNext = getNext(entry = this.entries[lastNext]);
                } while (lastNext != srcIndex);
                this.entries[previous] = swapNext(entry, dstIndex);
            }
        }
        else {
            this.keys[dstIndex] = null;
            this.values[dstIndex] = null;
            this.entries[dstIndex] = -1L;
        }
    }
    
    int firstEntryIndex() {
        return this.isEmpty() ? -1 : 0;
    }
    
    int getSuccessor(final int entryIndex) {
        return (entryIndex + 1 < this.size) ? (entryIndex + 1) : -1;
    }
    
    int adjustAfterRemove(final int indexBeforeRemove, final int indexRemoved) {
        return indexBeforeRemove - 1;
    }
    
    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        Preconditions.checkNotNull(function);
        for (int i = 0; i < this.size; ++i) {
            this.values[i] = function.apply((Object)this.keys[i], (Object)this.values[i]);
        }
    }
    
    @Override
    public Set<K> keySet() {
        return (this.keySetView == null) ? (this.keySetView = this.createKeySet()) : this.keySetView;
    }
    
    Set<K> createKeySet() {
        return (Set<K>)new KeySetView();
    }
    
    Iterator<K> keySetIterator() {
        return new Itr<K>() {
            @Override
            K getOutput(final int entry) {
                return (K)CompactHashMap.this.keys[entry];
            }
        };
    }
    
    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        for (int i = 0; i < this.size; ++i) {
            action.accept((Object)this.keys[i], (Object)this.values[i]);
        }
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return (this.entrySetView == null) ? (this.entrySetView = this.createEntrySet()) : this.entrySetView;
    }
    
    Set<Map.Entry<K, V>> createEntrySet() {
        return (Set<Map.Entry<K, V>>)new EntrySetView();
    }
    
    Iterator<Map.Entry<K, V>> entrySetIterator() {
        return new Itr<Map.Entry<K, V>>() {
            @Override
            Map.Entry<K, V> getOutput(final int entry) {
                return new MapEntry(entry);
            }
        };
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        for (int i = 0; i < this.size; ++i) {
            if (Objects.equal(value, this.values[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Collection<V> values() {
        return (this.valuesView == null) ? (this.valuesView = this.createValues()) : this.valuesView;
    }
    
    Collection<V> createValues() {
        return (Collection<V>)new ValuesView();
    }
    
    Iterator<V> valuesIterator() {
        return new Itr<V>() {
            @Override
            V getOutput(final int entry) {
                return (V)CompactHashMap.this.values[entry];
            }
        };
    }
    
    public void trimToSize() {
        final int size = this.size;
        if (size < this.entries.length) {
            this.resizeEntries(size);
        }
        int minimumTableSize = Math.max(1, Integer.highestOneBit((int)(size / this.loadFactor)));
        if (minimumTableSize < 1073741824) {
            final double load = size / (double)minimumTableSize;
            if (load > this.loadFactor) {
                minimumTableSize <<= 1;
            }
        }
        if (minimumTableSize < this.table.length) {
            this.resizeTable(minimumTableSize);
        }
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        Arrays.fill(this.keys, 0, this.size, null);
        Arrays.fill(this.values, 0, this.size, null);
        Arrays.fill(this.table, -1);
        Arrays.fill(this.entries, -1L);
        this.size = 0;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            stream.writeObject(this.keys[i]);
            stream.writeObject(this.values[i]);
        }
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.init(3, 1.0f);
        int i;
        final int elementCount = i = stream.readInt();
        while (--i >= 0) {
            final K key = (K)stream.readObject();
            final V value = (V)stream.readObject();
            this.put(key, value);
        }
    }
    
    private abstract class Itr<T> implements Iterator<T>
    {
        int expectedModCount;
        int currentIndex;
        int indexToRemove;
        
        private Itr() {
            this.expectedModCount = CompactHashMap.this.modCount;
            this.currentIndex = CompactHashMap.this.firstEntryIndex();
            this.indexToRemove = -1;
        }
        
        @Override
        public boolean hasNext() {
            return this.currentIndex >= 0;
        }
        
        abstract T getOutput(final int p0);
        
        @Override
        public T next() {
            this.checkForConcurrentModification();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.indexToRemove = this.currentIndex;
            final T result = this.getOutput(this.currentIndex);
            this.currentIndex = CompactHashMap.this.getSuccessor(this.currentIndex);
            return result;
        }
        
        @Override
        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.indexToRemove >= 0);
            ++this.expectedModCount;
            CompactHashMap.this.removeEntry(this.indexToRemove);
            this.currentIndex = CompactHashMap.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
            this.indexToRemove = -1;
        }
        
        private void checkForConcurrentModification() {
            if (CompactHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    class KeySetView extends Maps.KeySet<K, V>
    {
        KeySetView() {
            super(CompactHashMap.this);
        }
        
        @Override
        public Object[] toArray() {
            return ObjectArrays.copyAsObjectArray(CompactHashMap.this.keys, 0, CompactHashMap.this.size);
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return ObjectArrays.toArrayImpl(CompactHashMap.this.keys, 0, CompactHashMap.this.size, a);
        }
        
        @Override
        public boolean remove(final Object o) {
            final int index = CompactHashMap.this.indexOf(o);
            if (index == -1) {
                return false;
            }
            CompactHashMap.this.removeEntry(index);
            return true;
        }
        
        @Override
        public Iterator<K> iterator() {
            return CompactHashMap.this.keySetIterator();
        }
        
        @Override
        public Spliterator<K> spliterator() {
            return Spliterators.spliterator(CompactHashMap.this.keys, 0, CompactHashMap.this.size, 17);
        }
        
        @Override
        public void forEach(final Consumer<? super K> action) {
            Preconditions.checkNotNull(action);
            for (int i = 0; i < CompactHashMap.this.size; ++i) {
                action.accept((Object)CompactHashMap.this.keys[i]);
            }
        }
    }
    
    class EntrySetView extends Maps.EntrySet<K, V>
    {
        @Override
        Map<K, V> map() {
            return (Map<K, V>)CompactHashMap.this;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return CompactHashMap.this.entrySetIterator();
        }
        
        @Override
        public Spliterator<Map.Entry<K, V>> spliterator() {
            return (Spliterator<Map.Entry<K, V>>)CollectSpliterators.indexed(CompactHashMap.this.size, 17, x$0 -> new MapEntry(x$0));
        }
        
        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                final int index = CompactHashMap.this.indexOf(entry.getKey());
                return index != -1 && Objects.equal(CompactHashMap.this.values[index], entry.getValue());
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                final int index = CompactHashMap.this.indexOf(entry.getKey());
                if (index != -1 && Objects.equal(CompactHashMap.this.values[index], entry.getValue())) {
                    CompactHashMap.this.removeEntry(index);
                    return true;
                }
            }
            return false;
        }
    }
    
    final class MapEntry extends AbstractMapEntry<K, V>
    {
        private final K key;
        private int lastKnownIndex;
        
        MapEntry(final int index) {
            this.key = (K)CompactHashMap.this.keys[index];
            this.lastKnownIndex = index;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        private void updateLastKnownIndex() {
            if (this.lastKnownIndex == -1 || this.lastKnownIndex >= CompactHashMap.this.size() || !Objects.equal(this.key, CompactHashMap.this.keys[this.lastKnownIndex])) {
                this.lastKnownIndex = CompactHashMap.this.indexOf(this.key);
            }
        }
        
        @Override
        public V getValue() {
            this.updateLastKnownIndex();
            return (V)((this.lastKnownIndex == -1) ? null : CompactHashMap.this.values[this.lastKnownIndex]);
        }
        
        @Override
        public V setValue(final V value) {
            this.updateLastKnownIndex();
            if (this.lastKnownIndex == -1) {
                CompactHashMap.this.put(this.key, value);
                return null;
            }
            final V old = (V)CompactHashMap.this.values[this.lastKnownIndex];
            CompactHashMap.this.values[this.lastKnownIndex] = value;
            return old;
        }
    }
    
    class ValuesView extends Maps.Values<K, V>
    {
        ValuesView() {
            super(CompactHashMap.this);
        }
        
        @Override
        public Iterator<V> iterator() {
            return CompactHashMap.this.valuesIterator();
        }
        
        @Override
        public void forEach(final Consumer<? super V> action) {
            Preconditions.checkNotNull(action);
            for (int i = 0; i < CompactHashMap.this.size; ++i) {
                action.accept((Object)CompactHashMap.this.values[i]);
            }
        }
        
        @Override
        public Spliterator<V> spliterator() {
            return Spliterators.spliterator(CompactHashMap.this.values, 0, CompactHashMap.this.size, 16);
        }
        
        @Override
        public Object[] toArray() {
            return ObjectArrays.copyAsObjectArray(CompactHashMap.this.values, 0, CompactHashMap.this.size);
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return ObjectArrays.toArrayImpl(CompactHashMap.this.values, 0, CompactHashMap.this.size, a);
        }
    }
}
