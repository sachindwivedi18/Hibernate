// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Consumer;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.base.Objects;
import java.util.Arrays;
import rp.com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Collection;
import rp.com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.AbstractSet;

@GwtIncompatible
class CompactHashSet<E> extends AbstractSet<E> implements Serializable
{
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final float DEFAULT_LOAD_FACTOR = 1.0f;
    private static final long NEXT_MASK = 4294967295L;
    private static final long HASH_MASK = -4294967296L;
    private static final int DEFAULT_SIZE = 3;
    static final int UNSET = -1;
    private transient int[] table;
    private transient long[] entries;
    transient Object[] elements;
    transient float loadFactor;
    transient int modCount;
    private transient int threshold;
    private transient int size;
    
    public static <E> CompactHashSet<E> create() {
        return new CompactHashSet<E>();
    }
    
    public static <E> CompactHashSet<E> create(final Collection<? extends E> collection) {
        final CompactHashSet<E> set = createWithExpectedSize(collection.size());
        set.addAll((Collection<?>)collection);
        return set;
    }
    
    public static <E> CompactHashSet<E> create(final E... elements) {
        final CompactHashSet<E> set = createWithExpectedSize(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
    
    public static <E> CompactHashSet<E> createWithExpectedSize(final int expectedSize) {
        return new CompactHashSet<E>(expectedSize);
    }
    
    CompactHashSet() {
        this.init(3, 1.0f);
    }
    
    CompactHashSet(final int expectedSize) {
        this.init(expectedSize, 1.0f);
    }
    
    void init(final int expectedSize, final float loadFactor) {
        Preconditions.checkArgument(expectedSize >= 0, (Object)"Initial capacity must be non-negative");
        Preconditions.checkArgument(loadFactor > 0.0f, (Object)"Illegal load factor");
        final int buckets = Hashing.closedTableSize(expectedSize, loadFactor);
        this.table = newTable(buckets);
        this.loadFactor = loadFactor;
        this.elements = new Object[expectedSize];
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
    
    private static int getHash(final long entry) {
        return (int)(entry >>> 32);
    }
    
    private static int getNext(final long entry) {
        return (int)entry;
    }
    
    private static long swapNext(final long entry, final int newNext) {
        return (0xFFFFFFFF00000000L & entry) | (0xFFFFFFFFL & (long)newNext);
    }
    
    private int hashTableMask() {
        return this.table.length - 1;
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean add(final E object) {
        final long[] entries = this.entries;
        final Object[] elements = this.elements;
        final int hash = Hashing.smearedHash(object);
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
                if (getHash(entry) == hash && Objects.equal(object, elements[next])) {
                    return false;
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
        this.insertEntry(newEntryIndex, object, hash);
        this.size = newSize;
        if (newEntryIndex >= this.threshold) {
            this.resizeTable(2 * this.table.length);
        }
        ++this.modCount;
        return true;
    }
    
    void insertEntry(final int entryIndex, final E object, final int hash) {
        this.entries[entryIndex] = ((long)hash << 32 | 0xFFFFFFFFL);
        this.elements[entryIndex] = object;
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
        this.elements = Arrays.copyOf(this.elements, newCapacity);
        long[] entries = this.entries;
        final int oldSize = entries.length;
        entries = Arrays.copyOf(entries, newCapacity);
        if (newCapacity > oldSize) {
            Arrays.fill(entries, oldSize, newCapacity, -1L);
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
    
    @Override
    public boolean contains(final Object object) {
        final int hash = Hashing.smearedHash(object);
        long entry;
        for (int next = this.table[hash & this.hashTableMask()]; next != -1; next = getNext(entry)) {
            entry = this.entries[next];
            if (getHash(entry) == hash && Objects.equal(object, this.elements[next])) {
                return true;
            }
        }
        return false;
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object object) {
        return this.remove(object, Hashing.smearedHash(object));
    }
    
    @CanIgnoreReturnValue
    private boolean remove(final Object object, final int hash) {
        final int tableIndex = hash & this.hashTableMask();
        int next = this.table[tableIndex];
        if (next == -1) {
            return false;
        }
        int last = -1;
        while (getHash(this.entries[next]) != hash || !Objects.equal(object, this.elements[next])) {
            last = next;
            next = getNext(this.entries[next]);
            if (next == -1) {
                return false;
            }
        }
        if (last == -1) {
            this.table[tableIndex] = getNext(this.entries[next]);
        }
        else {
            this.entries[last] = swapNext(this.entries[last], getNext(this.entries[next]));
        }
        this.moveEntry(next);
        --this.size;
        ++this.modCount;
        return true;
    }
    
    void moveEntry(final int dstIndex) {
        final int srcIndex = this.size() - 1;
        if (dstIndex < srcIndex) {
            this.elements[dstIndex] = this.elements[srcIndex];
            this.elements[srcIndex] = null;
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
            this.elements[dstIndex] = null;
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
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int expectedModCount = CompactHashSet.this.modCount;
            int index = CompactHashSet.this.firstEntryIndex();
            int indexToRemove = -1;
            
            @Override
            public boolean hasNext() {
                return this.index >= 0;
            }
            
            @Override
            public E next() {
                this.checkForConcurrentModification();
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.indexToRemove = this.index;
                final E result = (E)CompactHashSet.this.elements[this.index];
                this.index = CompactHashSet.this.getSuccessor(this.index);
                return result;
            }
            
            @Override
            public void remove() {
                this.checkForConcurrentModification();
                CollectPreconditions.checkRemove(this.indexToRemove >= 0);
                ++this.expectedModCount;
                CompactHashSet.this.remove(CompactHashSet.this.elements[this.indexToRemove], getHash(CompactHashSet.this.entries[this.indexToRemove]));
                this.index = CompactHashSet.this.adjustAfterRemove(this.index, this.indexToRemove);
                this.indexToRemove = -1;
            }
            
            private void checkForConcurrentModification() {
                if (CompactHashSet.this.modCount != this.expectedModCount) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this.elements, 0, this.size, 17);
    }
    
    @Override
    public void forEach(final Consumer<? super E> action) {
        Preconditions.checkNotNull(action);
        for (int i = 0; i < this.size; ++i) {
            action.accept((Object)this.elements[i]);
        }
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
    public Object[] toArray() {
        return Arrays.copyOf(this.elements, this.size);
    }
    
    @CanIgnoreReturnValue
    @Override
    public <T> T[] toArray(final T[] a) {
        return ObjectArrays.toArrayImpl(this.elements, 0, this.size, a);
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
        Arrays.fill(this.elements, 0, this.size, null);
        Arrays.fill(this.table, -1);
        Arrays.fill(this.entries, -1L);
        this.size = 0;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.size);
        for (final E e : this) {
            stream.writeObject(e);
        }
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.init(3, 1.0f);
        int i;
        final int elementCount = i = stream.readInt();
        while (--i >= 0) {
            final E element = (E)stream.readObject();
            this.add(element);
        }
    }
}
