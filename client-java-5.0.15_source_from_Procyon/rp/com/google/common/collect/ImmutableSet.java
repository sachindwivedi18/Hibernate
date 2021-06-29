// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Arrays;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.Spliterator;
import rp.com.google.common.math.IntMath;
import java.math.RoundingMode;
import rp.com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.SortedSet;
import java.util.Collection;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.annotations.Beta;
import java.util.stream.Collector;
import com.google.j2objc.annotations.RetainedWith;
import com.google.errorprone.annotations.concurrent.LazyInit;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.Set;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E>
{
    static final int SPLITERATOR_CHARACTERISTICS = 1297;
    @LazyInit
    @RetainedWith
    private transient ImmutableList<E> asList;
    static final int MAX_TABLE_SIZE = 1073741824;
    private static final double DESIRED_LOAD_FACTOR = 0.7;
    private static final int CUTOFF = 751619276;
    static final double HASH_FLOODING_FPP = 0.001;
    static final int MAX_RUN_MULTIPLIER = 12;
    
    @Beta
    public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
        return CollectCollectors.toImmutableSet();
    }
    
    public static <E> ImmutableSet<E> of() {
        return (ImmutableSet<E>)RegularImmutableSet.EMPTY;
    }
    
    public static <E> ImmutableSet<E> of(final E element) {
        return new SingletonImmutableSet<E>(element);
    }
    
    public static <E> ImmutableSet<E> of(final E e1, final E e2) {
        return construct(2, e1, e2);
    }
    
    public static <E> ImmutableSet<E> of(final E e1, final E e2, final E e3) {
        return construct(3, e1, e2, e3);
    }
    
    public static <E> ImmutableSet<E> of(final E e1, final E e2, final E e3, final E e4) {
        return construct(4, e1, e2, e3, e4);
    }
    
    public static <E> ImmutableSet<E> of(final E e1, final E e2, final E e3, final E e4, final E e5) {
        return construct(5, e1, e2, e3, e4, e5);
    }
    
    @SafeVarargs
    public static <E> ImmutableSet<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E... others) {
        Preconditions.checkArgument(others.length <= 2147483641, (Object)"the total number of elements must fit in an int");
        final int paramCount = 6;
        final Object[] elements = new Object[6 + others.length];
        elements[0] = e1;
        elements[1] = e2;
        elements[2] = e3;
        elements[3] = e4;
        elements[4] = e5;
        elements[5] = e6;
        System.arraycopy(others, 0, elements, 6, others.length);
        return construct(elements.length, elements);
    }
    
    private static <E> ImmutableSet<E> construct(final int n, final Object... elements) {
        switch (n) {
            case 0: {
                return of();
            }
            case 1: {
                final E elem = (E)elements[0];
                return of(elem);
            }
            default: {
                SetBuilderImpl<E> builder = new RegularSetBuilderImpl<E>(4);
                for (int i = 0; i < n; ++i) {
                    final E e = Preconditions.checkNotNull(elements[i]);
                    builder = builder.add(e);
                }
                return builder.review().build();
            }
        }
    }
    
    public static <E> ImmutableSet<E> copyOf(final Collection<? extends E> elements) {
        if (elements instanceof ImmutableSet && !(elements instanceof SortedSet)) {
            final ImmutableSet<E> set = (ImmutableSet<E>)(ImmutableSet)elements;
            if (!set.isPartialView()) {
                return set;
            }
        }
        else if (elements instanceof EnumSet) {
            return (ImmutableSet<E>)copyOfEnumSet((EnumSet)elements);
        }
        final Object[] array = elements.toArray();
        return construct(array.length, array);
    }
    
    public static <E> ImmutableSet<E> copyOf(final Iterable<? extends E> elements) {
        return (elements instanceof Collection) ? copyOf((Collection<? extends E>)(Collection)elements) : copyOf(elements.iterator());
    }
    
    public static <E> ImmutableSet<E> copyOf(final Iterator<? extends E> elements) {
        if (!elements.hasNext()) {
            return of();
        }
        final E first = (E)elements.next();
        if (!elements.hasNext()) {
            return of(first);
        }
        return new Builder<E>().add(first).addAll(elements).build();
    }
    
    public static <E> ImmutableSet<E> copyOf(final E[] elements) {
        switch (elements.length) {
            case 0: {
                return of();
            }
            case 1: {
                return of(elements[0]);
            }
            default: {
                return construct(elements.length, (Object[])elements.clone());
            }
        }
    }
    
    private static ImmutableSet copyOfEnumSet(final EnumSet enumSet) {
        return ImmutableEnumSet.asImmutable(EnumSet.copyOf((EnumSet<Enum>)enumSet));
    }
    
    ImmutableSet() {
    }
    
    boolean isHashCodeFast() {
        return false;
    }
    
    @Override
    public boolean equals(final Object object) {
        return object == this || ((!(object instanceof ImmutableSet) || !this.isHashCodeFast() || !((ImmutableSet)object).isHashCodeFast() || this.hashCode() == object.hashCode()) && Sets.equalsImpl(this, object));
    }
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }
    
    @Override
    public abstract UnmodifiableIterator<E> iterator();
    
    @Override
    public ImmutableList<E> asList() {
        final ImmutableList<E> result = this.asList;
        return (result == null) ? (this.asList = this.createAsList()) : result;
    }
    
    ImmutableList<E> createAsList() {
        return new RegularImmutableAsList<E>(this, this.toArray());
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    @Beta
    public static <E> Builder<E> builderWithExpectedSize(final int expectedSize) {
        CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
        return new Builder<E>(expectedSize);
    }
    
    static Object[] rebuildHashTable(final int newTableSize, final Object[] elements, final int n) {
        final Object[] hashTable = new Object[newTableSize];
        final int mask = hashTable.length - 1;
        for (final Object e : elements) {
            int k;
            final int j0 = k = Hashing.smear(e.hashCode());
            int index;
            while (true) {
                index = (k & mask);
                if (hashTable[index] == null) {
                    break;
                }
                ++k;
            }
            hashTable[index] = e;
        }
        return hashTable;
    }
    
    @VisibleForTesting
    static int chooseTableSize(int setSize) {
        setSize = Math.max(setSize, 2);
        if (setSize < 751619276) {
            int tableSize;
            for (tableSize = Integer.highestOneBit(setSize - 1) << 1; tableSize * 0.7 < setSize; tableSize <<= 1) {}
            return tableSize;
        }
        Preconditions.checkArgument(setSize < 1073741824, (Object)"collection too large");
        return 1073741824;
    }
    
    static boolean hashFloodingDetected(final Object[] hashTable) {
        final int maxRunBeforeFallback = maxRunBeforeFallback(hashTable.length);
        int endOfStartRun = 0;
        while (endOfStartRun < hashTable.length && hashTable[endOfStartRun] != null) {
            if (++endOfStartRun > maxRunBeforeFallback) {
                return true;
            }
        }
        int startOfEndRun;
        for (startOfEndRun = hashTable.length - 1; startOfEndRun > endOfStartRun && hashTable[startOfEndRun] != null; --startOfEndRun) {
            if (endOfStartRun + (hashTable.length - 1 - startOfEndRun) > maxRunBeforeFallback) {
                return true;
            }
        }
        for (int i = endOfStartRun + 1; i < startOfEndRun; ++i) {
            int runLength = 0;
            while (i < startOfEndRun && hashTable[i] != null) {
                if (++runLength > maxRunBeforeFallback) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }
    
    static int maxRunBeforeFallback(final int tableSize) {
        return 12 * IntMath.log2(tableSize, RoundingMode.UNNECESSARY);
    }
    
    abstract static class Indexed<E> extends ImmutableSet<E>
    {
        abstract E get(final int p0);
        
        @Override
        public UnmodifiableIterator<E> iterator() {
            return this.asList().iterator();
        }
        
        @Override
        public Spliterator<E> spliterator() {
            return CollectSpliterators.indexed(this.size(), 1297, this::get);
        }
        
        @Override
        public void forEach(final Consumer<? super E> consumer) {
            Preconditions.checkNotNull(consumer);
            for (int n = this.size(), i = 0; i < n; ++i) {
                consumer.accept(this.get(i));
            }
        }
        
        @Override
        int copyIntoArray(final Object[] dst, final int offset) {
            return this.asList().copyIntoArray(dst, offset);
        }
        
        @Override
        ImmutableList<E> createAsList() {
            return new ImmutableAsList<E>() {
                @Override
                public E get(final int index) {
                    return Indexed.this.get(index);
                }
                
                @Override
                Indexed<E> delegateCollection() {
                    return Indexed.this;
                }
            };
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        final Object[] elements;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return ImmutableSet.copyOf(this.elements);
        }
    }
    
    public static class Builder<E> extends ImmutableCollection.Builder<E>
    {
        private SetBuilderImpl<E> impl;
        boolean forceCopy;
        
        public Builder() {
            this(4);
        }
        
        Builder(final int capacity) {
            this.impl = new RegularSetBuilderImpl<E>(capacity);
        }
        
        Builder(final boolean subclass) {
            this.impl = null;
        }
        
        @VisibleForTesting
        void forceJdk() {
            this.impl = new JdkBackedSetBuilderImpl<E>(this.impl);
        }
        
        final void copyIfNecessary() {
            if (this.forceCopy) {
                this.copy();
                this.forceCopy = false;
            }
        }
        
        void copy() {
            this.impl = this.impl.copy();
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> add(final E element) {
            Preconditions.checkNotNull(element);
            this.copyIfNecessary();
            this.impl = this.impl.add(element);
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> add(final E... elements) {
            super.add(elements);
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> addAll(final Iterable<? extends E> elements) {
            super.addAll(elements);
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> addAll(final Iterator<? extends E> elements) {
            super.addAll(elements);
            return this;
        }
        
        Builder<E> combine(final Builder<E> other) {
            this.copyIfNecessary();
            this.impl = this.impl.combine(other.impl);
            return this;
        }
        
        @Override
        public ImmutableSet<E> build() {
            this.forceCopy = true;
            this.impl = this.impl.review();
            return this.impl.build();
        }
    }
    
    private abstract static class SetBuilderImpl<E>
    {
        E[] dedupedElements;
        int distinct;
        
        SetBuilderImpl(final int expectedCapacity) {
            this.dedupedElements = (E[])new Object[expectedCapacity];
            this.distinct = 0;
        }
        
        SetBuilderImpl(final SetBuilderImpl<E> toCopy) {
            this.dedupedElements = Arrays.copyOf(toCopy.dedupedElements, toCopy.dedupedElements.length);
            this.distinct = toCopy.distinct;
        }
        
        private void ensureCapacity(final int minCapacity) {
            if (minCapacity > this.dedupedElements.length) {
                final int newCapacity = ImmutableCollection.Builder.expandedCapacity(this.dedupedElements.length, minCapacity);
                this.dedupedElements = Arrays.copyOf(this.dedupedElements, newCapacity);
            }
        }
        
        final void addDedupedElement(final E e) {
            this.ensureCapacity(this.distinct + 1);
            this.dedupedElements[this.distinct++] = e;
        }
        
        abstract SetBuilderImpl<E> add(final E p0);
        
        final SetBuilderImpl<E> combine(final SetBuilderImpl<E> other) {
            SetBuilderImpl<E> result = this;
            for (int i = 0; i < other.distinct; ++i) {
                result = result.add(other.dedupedElements[i]);
            }
            return result;
        }
        
        abstract SetBuilderImpl<E> copy();
        
        SetBuilderImpl<E> review() {
            return this;
        }
        
        abstract ImmutableSet<E> build();
    }
    
    private static final class RegularSetBuilderImpl<E> extends SetBuilderImpl<E>
    {
        private Object[] hashTable;
        private int maxRunBeforeFallback;
        private int expandTableThreshold;
        private int hashCode;
        
        RegularSetBuilderImpl(final int expectedCapacity) {
            super(expectedCapacity);
            final int tableSize = ImmutableSet.chooseTableSize(expectedCapacity);
            this.hashTable = new Object[tableSize];
            this.maxRunBeforeFallback = ImmutableSet.maxRunBeforeFallback(tableSize);
            this.expandTableThreshold = (int)(0.7 * tableSize);
        }
        
        RegularSetBuilderImpl(final RegularSetBuilderImpl<E> toCopy) {
            super(toCopy);
            this.hashTable = Arrays.copyOf(toCopy.hashTable, toCopy.hashTable.length);
            this.maxRunBeforeFallback = toCopy.maxRunBeforeFallback;
            this.expandTableThreshold = toCopy.expandTableThreshold;
            this.hashCode = toCopy.hashCode;
        }
        
        void ensureTableCapacity(final int minCapacity) {
            if (minCapacity > this.expandTableThreshold && this.hashTable.length < 1073741824) {
                final int newTableSize = this.hashTable.length * 2;
                this.hashTable = ImmutableSet.rebuildHashTable(newTableSize, this.dedupedElements, this.distinct);
                this.maxRunBeforeFallback = ImmutableSet.maxRunBeforeFallback(newTableSize);
                this.expandTableThreshold = (int)(0.7 * newTableSize);
            }
        }
        
        @Override
        SetBuilderImpl<E> add(final E e) {
            Preconditions.checkNotNull(e);
            final int eHash = e.hashCode();
            final int i0 = Hashing.smear(eHash);
            final int mask = this.hashTable.length - 1;
            for (int j = i0; j - i0 < this.maxRunBeforeFallback; ++j) {
                final int index = j & mask;
                final Object tableEntry = this.hashTable[index];
                if (tableEntry == null) {
                    this.addDedupedElement(e);
                    this.hashTable[index] = e;
                    this.hashCode += eHash;
                    this.ensureTableCapacity(this.distinct);
                    return this;
                }
                if (tableEntry.equals(e)) {
                    return this;
                }
            }
            return new JdkBackedSetBuilderImpl<E>(this).add(e);
        }
        
        @Override
        SetBuilderImpl<E> copy() {
            return new RegularSetBuilderImpl((RegularSetBuilderImpl<Object>)this);
        }
        
        @Override
        SetBuilderImpl<E> review() {
            final int targetTableSize = ImmutableSet.chooseTableSize(this.distinct);
            if (targetTableSize * 2 < this.hashTable.length) {
                this.hashTable = ImmutableSet.rebuildHashTable(targetTableSize, this.dedupedElements, this.distinct);
            }
            return (SetBuilderImpl<E>)(ImmutableSet.hashFloodingDetected(this.hashTable) ? new JdkBackedSetBuilderImpl<E>((SetBuilderImpl<Object>)this) : this);
        }
        
        @Override
        ImmutableSet<E> build() {
            switch (this.distinct) {
                case 0: {
                    return ImmutableSet.of();
                }
                case 1: {
                    return ImmutableSet.of(this.dedupedElements[0]);
                }
                default: {
                    final Object[] elements = (this.distinct == this.dedupedElements.length) ? this.dedupedElements : Arrays.copyOf(this.dedupedElements, this.distinct);
                    return new RegularImmutableSet<E>(elements, this.hashCode, this.hashTable, this.hashTable.length - 1);
                }
            }
        }
    }
    
    private static final class JdkBackedSetBuilderImpl<E> extends SetBuilderImpl<E>
    {
        private final Set<Object> delegate;
        
        JdkBackedSetBuilderImpl(final SetBuilderImpl<E> toCopy) {
            super(toCopy);
            this.delegate = Sets.newHashSetWithExpectedSize(this.distinct);
            for (int i = 0; i < this.distinct; ++i) {
                this.delegate.add(this.dedupedElements[i]);
            }
        }
        
        @Override
        SetBuilderImpl<E> add(final E e) {
            Preconditions.checkNotNull(e);
            if (this.delegate.add(e)) {
                this.addDedupedElement(e);
            }
            return this;
        }
        
        @Override
        SetBuilderImpl<E> copy() {
            return new JdkBackedSetBuilderImpl((SetBuilderImpl<Object>)this);
        }
        
        @Override
        ImmutableSet<E> build() {
            switch (this.distinct) {
                case 0: {
                    return ImmutableSet.of();
                }
                case 1: {
                    return ImmutableSet.of(this.dedupedElements[0]);
                }
                default: {
                    return new JdkBackedImmutableSet<E>(this.delegate, ImmutableList.asImmutableList(this.dedupedElements, this.distinct));
                }
            }
        }
    }
}
