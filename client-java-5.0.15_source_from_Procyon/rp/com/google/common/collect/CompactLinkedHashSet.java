// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.base.Preconditions;
import java.util.function.Consumer;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
class CompactLinkedHashSet<E> extends CompactHashSet<E>
{
    private static final int ENDPOINT = -2;
    private transient int[] predecessor;
    private transient int[] successor;
    private transient int firstEntry;
    private transient int lastEntry;
    
    public static <E> CompactLinkedHashSet<E> create() {
        return new CompactLinkedHashSet<E>();
    }
    
    public static <E> CompactLinkedHashSet<E> create(final Collection<? extends E> collection) {
        final CompactLinkedHashSet<E> set = createWithExpectedSize(collection.size());
        set.addAll((Collection<?>)collection);
        return set;
    }
    
    public static <E> CompactLinkedHashSet<E> create(final E... elements) {
        final CompactLinkedHashSet<E> set = createWithExpectedSize(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
    
    public static <E> CompactLinkedHashSet<E> createWithExpectedSize(final int expectedSize) {
        return new CompactLinkedHashSet<E>(expectedSize);
    }
    
    CompactLinkedHashSet() {
    }
    
    CompactLinkedHashSet(final int expectedSize) {
        super(expectedSize);
    }
    
    @Override
    void init(final int expectedSize, final float loadFactor) {
        super.init(expectedSize, loadFactor);
        this.predecessor = new int[expectedSize];
        this.successor = new int[expectedSize];
        Arrays.fill(this.predecessor, -1);
        Arrays.fill(this.successor, -1);
        this.firstEntry = -2;
        this.lastEntry = -2;
    }
    
    private void succeeds(final int pred, final int succ) {
        if (pred == -2) {
            this.firstEntry = succ;
        }
        else {
            this.successor[pred] = succ;
        }
        if (succ == -2) {
            this.lastEntry = pred;
        }
        else {
            this.predecessor[succ] = pred;
        }
    }
    
    @Override
    void insertEntry(final int entryIndex, final E object, final int hash) {
        super.insertEntry(entryIndex, object, hash);
        this.succeeds(this.lastEntry, entryIndex);
        this.succeeds(entryIndex, -2);
    }
    
    @Override
    void moveEntry(final int dstIndex) {
        final int srcIndex = this.size() - 1;
        super.moveEntry(dstIndex);
        this.succeeds(this.predecessor[dstIndex], this.successor[dstIndex]);
        if (srcIndex != dstIndex) {
            this.succeeds(this.predecessor[srcIndex], dstIndex);
            this.succeeds(dstIndex, this.successor[srcIndex]);
        }
        this.predecessor[srcIndex] = -1;
        this.successor[srcIndex] = -1;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.firstEntry = -2;
        this.lastEntry = -2;
        Arrays.fill(this.predecessor, -1);
        Arrays.fill(this.successor, -1);
    }
    
    @Override
    void resizeEntries(final int newCapacity) {
        super.resizeEntries(newCapacity);
        final int oldCapacity = this.predecessor.length;
        this.predecessor = Arrays.copyOf(this.predecessor, newCapacity);
        this.successor = Arrays.copyOf(this.successor, newCapacity);
        if (oldCapacity < newCapacity) {
            Arrays.fill(this.predecessor, oldCapacity, newCapacity, -1);
            Arrays.fill(this.successor, oldCapacity, newCapacity, -1);
        }
    }
    
    @Override
    public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return ObjectArrays.toArrayImpl(this, a);
    }
    
    @Override
    int firstEntryIndex() {
        return this.firstEntry;
    }
    
    @Override
    int adjustAfterRemove(final int indexBeforeRemove, final int indexRemoved) {
        return (indexBeforeRemove == this.size()) ? indexRemoved : indexBeforeRemove;
    }
    
    @Override
    int getSuccessor(final int entryIndex) {
        return this.successor[entryIndex];
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator((Collection<? extends E>)this, 17);
    }
    
    @Override
    public void forEach(final Consumer<? super E> action) {
        Preconditions.checkNotNull(action);
        for (int i = this.firstEntry; i != -2; i = this.successor[i]) {
            action.accept((Object)this.elements[i]);
        }
    }
}
