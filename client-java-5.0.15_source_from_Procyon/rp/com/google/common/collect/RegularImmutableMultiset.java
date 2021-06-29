// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import rp.com.google.common.base.Objects;
import java.util.Iterator;
import rp.com.google.common.primitives.Ints;
import rp.com.google.common.base.Preconditions;
import java.util.Collection;
import com.google.errorprone.annotations.concurrent.LazyInit;
import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E>
{
    static final ImmutableMultiset<Object> EMPTY;
    @VisibleForTesting
    static final double MAX_LOAD_FACTOR = 1.0;
    @VisibleForTesting
    static final double HASH_FLOODING_FPP = 0.001;
    @VisibleForTesting
    static final int MAX_HASH_BUCKET_LENGTH = 9;
    private final transient Multisets.ImmutableEntry<E>[] entries;
    private final transient Multisets.ImmutableEntry<E>[] hashTable;
    private final transient int size;
    private final transient int hashCode;
    @LazyInit
    private transient ImmutableSet<E> elementSet;
    
    static <E> ImmutableMultiset<E> create(final Collection<? extends Multiset.Entry<? extends E>> entries) {
        final int distinct = entries.size();
        final Multisets.ImmutableEntry<E>[] entryArray = (Multisets.ImmutableEntry<E>[])new Multisets.ImmutableEntry[distinct];
        if (distinct == 0) {
            return new RegularImmutableMultiset<E>(entryArray, null, 0, 0, ImmutableSet.of());
        }
        final int tableSize = Hashing.closedTableSize(distinct, 1.0);
        final int mask = tableSize - 1;
        final Multisets.ImmutableEntry<E>[] hashTable = (Multisets.ImmutableEntry<E>[])new Multisets.ImmutableEntry[tableSize];
        int index = 0;
        int hashCode = 0;
        long size = 0L;
        for (final Multiset.Entry<? extends E> entry : entries) {
            final E element = Preconditions.checkNotNull((E)entry.getElement());
            final int count = entry.getCount();
            final int hash = element.hashCode();
            final int bucket = Hashing.smear(hash) & mask;
            final Multisets.ImmutableEntry<E> bucketHead = hashTable[bucket];
            Multisets.ImmutableEntry<E> newEntry;
            if (bucketHead == null) {
                final boolean canReuseEntry = entry instanceof Multisets.ImmutableEntry && !(entry instanceof NonTerminalEntry);
                newEntry = (canReuseEntry ? ((Multisets.ImmutableEntry)entry) : new Multisets.ImmutableEntry<E>((E)element, count));
            }
            else {
                newEntry = new NonTerminalEntry<E>(element, count, bucketHead);
            }
            hashCode += (hash ^ count);
            hashTable[bucket] = (entryArray[index++] = newEntry);
            size += count;
        }
        return hashFloodingDetected(hashTable) ? JdkBackedImmutableMultiset.create((Collection<? extends Multiset.Entry<? extends E>>)ImmutableList.asImmutableList(entryArray)) : new RegularImmutableMultiset<E>(entryArray, hashTable, Ints.saturatedCast(size), hashCode, null);
    }
    
    private static boolean hashFloodingDetected(final Multisets.ImmutableEntry<?>[] hashTable) {
        for (int i = 0; i < hashTable.length; ++i) {
            int bucketLength = 0;
            for (Multisets.ImmutableEntry<?> entry = hashTable[i]; entry != null; entry = entry.nextInBucket()) {
                if (++bucketLength > 9) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private RegularImmutableMultiset(final Multisets.ImmutableEntry<E>[] entries, final Multisets.ImmutableEntry<E>[] hashTable, final int size, final int hashCode, final ImmutableSet<E> elementSet) {
        this.entries = entries;
        this.hashTable = hashTable;
        this.size = size;
        this.hashCode = hashCode;
        this.elementSet = elementSet;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int count(final Object element) {
        final Multisets.ImmutableEntry<E>[] hashTable = this.hashTable;
        if (element == null || hashTable == null) {
            return 0;
        }
        final int hash = Hashing.smearedHash(element);
        final int mask = hashTable.length - 1;
        for (Multisets.ImmutableEntry<E> entry = hashTable[hash & mask]; entry != null; entry = entry.nextInBucket()) {
            if (Objects.equal(element, entry.getElement())) {
                return entry.getCount();
            }
        }
        return 0;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public ImmutableSet<E> elementSet() {
        final ImmutableSet<E> result = this.elementSet;
        return (result == null) ? (this.elementSet = new ElementSet<E>((List<Multiset.Entry<E>>)Arrays.asList(this.entries), this)) : result;
    }
    
    @Override
    Multiset.Entry<E> getEntry(final int index) {
        return this.entries[index];
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    static {
        EMPTY = create((Collection<? extends Multiset.Entry<?>>)ImmutableList.of());
    }
    
    private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E>
    {
        private final Multisets.ImmutableEntry<E> nextInBucket;
        
        NonTerminalEntry(final E element, final int count, final Multisets.ImmutableEntry<E> nextInBucket) {
            super(element, count);
            this.nextInBucket = nextInBucket;
        }
        
        @Override
        public Multisets.ImmutableEntry<E> nextInBucket() {
            return this.nextInBucket;
        }
    }
}
