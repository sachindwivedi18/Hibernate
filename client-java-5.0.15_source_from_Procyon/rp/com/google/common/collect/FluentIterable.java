// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.stream.Stream;
import rp.com.google.common.base.Joiner;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.List;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.base.Predicate;
import rp.com.google.common.base.Function;
import rp.com.google.common.annotations.Beta;
import java.util.Arrays;
import java.util.Iterator;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.base.Optional;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public abstract class FluentIterable<E> implements Iterable<E>
{
    private final Optional<Iterable<E>> iterableDelegate;
    
    protected FluentIterable() {
        this.iterableDelegate = Optional.absent();
    }
    
    FluentIterable(final Iterable<E> iterable) {
        Preconditions.checkNotNull(iterable);
        this.iterableDelegate = Optional.fromNullable((this != iterable) ? iterable : null);
    }
    
    private Iterable<E> getDelegate() {
        return this.iterableDelegate.or(this);
    }
    
    public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
        return (iterable instanceof FluentIterable) ? ((FluentIterable)iterable) : new FluentIterable<E>(iterable) {
            @Override
            public Iterator<E> iterator() {
                return iterable.iterator();
            }
        };
    }
    
    @Beta
    public static <E> FluentIterable<E> from(final E[] elements) {
        return from(Arrays.asList(elements));
    }
    
    @Deprecated
    public static <E> FluentIterable<E> from(final FluentIterable<E> iterable) {
        return Preconditions.checkNotNull(iterable);
    }
    
    @Beta
    public static <T> FluentIterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b) {
        return concatNoDefensiveCopy(a, b);
    }
    
    @Beta
    public static <T> FluentIterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b, final Iterable<? extends T> c) {
        return concatNoDefensiveCopy(a, b, c);
    }
    
    @Beta
    public static <T> FluentIterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b, final Iterable<? extends T> c, final Iterable<? extends T> d) {
        return concatNoDefensiveCopy(a, b, c, d);
    }
    
    @Beta
    public static <T> FluentIterable<T> concat(final Iterable<? extends T>... inputs) {
        return (FluentIterable<T>)concatNoDefensiveCopy((Iterable<?>[])Arrays.copyOf((Iterable<? extends T>[])inputs, inputs.length));
    }
    
    @Beta
    public static <T> FluentIterable<T> concat(final Iterable<? extends Iterable<? extends T>> inputs) {
        Preconditions.checkNotNull(inputs);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.concat(Iterators.transform(inputs.iterator(), (Function<? super Object, ? extends Iterator<? extends T>>)Iterables.toIterator()));
            }
        };
    }
    
    private static <T> FluentIterable<T> concatNoDefensiveCopy(final Iterable<? extends T>... inputs) {
        for (final Iterable<? extends T> input : inputs) {
            Preconditions.checkNotNull(input);
        }
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.concat((Iterator<? extends Iterator<? extends T>>)new AbstractIndexedListIterator<Iterator<? extends T>>(inputs.length) {
                    public Iterator<? extends T> get(final int i) {
                        return inputs[i].iterator();
                    }
                });
            }
        };
    }
    
    @Beta
    public static <E> FluentIterable<E> of() {
        return from((Iterable<E>)ImmutableList.of());
    }
    
    @Beta
    public static <E> FluentIterable<E> of(final E element, final E... elements) {
        return from(Lists.asList(element, elements));
    }
    
    @Override
    public String toString() {
        return Iterables.toString(this.getDelegate());
    }
    
    public final int size() {
        return Iterables.size(this.getDelegate());
    }
    
    public final boolean contains(final Object target) {
        return Iterables.contains(this.getDelegate(), target);
    }
    
    public final FluentIterable<E> cycle() {
        return from((Iterable<E>)Iterables.cycle(this.getDelegate()));
    }
    
    @Beta
    public final FluentIterable<E> append(final Iterable<? extends E> other) {
        return concat(this.getDelegate(), other);
    }
    
    @Beta
    public final FluentIterable<E> append(final E... elements) {
        return concat(this.getDelegate(), (Iterable<? extends E>)Arrays.asList(elements));
    }
    
    public final FluentIterable<E> filter(final Predicate<? super E> predicate) {
        return from((Iterable<E>)Iterables.filter(this.getDelegate(), (Predicate<? super E>)predicate));
    }
    
    @GwtIncompatible
    public final <T> FluentIterable<T> filter(final Class<T> type) {
        return from((Iterable<T>)Iterables.filter(this.getDelegate(), (Class<E>)type));
    }
    
    public final boolean anyMatch(final Predicate<? super E> predicate) {
        return Iterables.any(this.getDelegate(), (Predicate<? super Object>)predicate);
    }
    
    public final boolean allMatch(final Predicate<? super E> predicate) {
        return Iterables.all(this.getDelegate(), (Predicate<? super Object>)predicate);
    }
    
    public final Optional<E> firstMatch(final Predicate<? super E> predicate) {
        return Iterables.tryFind(this.getDelegate(), predicate);
    }
    
    public final <T> FluentIterable<T> transform(final Function<? super E, T> function) {
        return from(Iterables.transform(this.getDelegate(), (Function<? super Object, ? extends T>)function));
    }
    
    public <T> FluentIterable<T> transformAndConcat(final Function<? super E, ? extends Iterable<? extends T>> function) {
        return concat((Iterable<? extends Iterable<? extends T>>)this.transform(function));
    }
    
    public final Optional<E> first() {
        final Iterator<E> iterator = this.getDelegate().iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
    }
    
    public final Optional<E> last() {
        final Iterable<E> iterable = this.getDelegate();
        if (iterable instanceof List) {
            final List<E> list = (List<E>)(List)iterable;
            if (list.isEmpty()) {
                return Optional.absent();
            }
            return Optional.of(list.get(list.size() - 1));
        }
        else {
            final Iterator<E> iterator = iterable.iterator();
            if (!iterator.hasNext()) {
                return Optional.absent();
            }
            if (iterable instanceof SortedSet) {
                final SortedSet<E> sortedSet = (SortedSet<E>)(SortedSet)iterable;
                return Optional.of(sortedSet.last());
            }
            E current;
            do {
                current = iterator.next();
            } while (iterator.hasNext());
            return Optional.of(current);
        }
    }
    
    public final FluentIterable<E> skip(final int numberToSkip) {
        return from((Iterable<E>)Iterables.skip(this.getDelegate(), numberToSkip));
    }
    
    public final FluentIterable<E> limit(final int maxSize) {
        return from((Iterable<E>)Iterables.limit(this.getDelegate(), maxSize));
    }
    
    public final boolean isEmpty() {
        return !this.getDelegate().iterator().hasNext();
    }
    
    public final ImmutableList<E> toList() {
        return ImmutableList.copyOf(this.getDelegate());
    }
    
    public final ImmutableList<E> toSortedList(final Comparator<? super E> comparator) {
        return Ordering.from(comparator).immutableSortedCopy(this.getDelegate());
    }
    
    public final ImmutableSet<E> toSet() {
        return ImmutableSet.copyOf(this.getDelegate());
    }
    
    public final ImmutableSortedSet<E> toSortedSet(final Comparator<? super E> comparator) {
        return ImmutableSortedSet.copyOf(comparator, this.getDelegate());
    }
    
    public final ImmutableMultiset<E> toMultiset() {
        return ImmutableMultiset.copyOf(this.getDelegate());
    }
    
    public final <V> ImmutableMap<E, V> toMap(final Function<? super E, V> valueFunction) {
        return Maps.toMap(this.getDelegate(), valueFunction);
    }
    
    public final <K> ImmutableListMultimap<K, E> index(final Function<? super E, K> keyFunction) {
        return Multimaps.index(this.getDelegate(), keyFunction);
    }
    
    public final <K> ImmutableMap<K, E> uniqueIndex(final Function<? super E, K> keyFunction) {
        return Maps.uniqueIndex(this.getDelegate(), keyFunction);
    }
    
    @GwtIncompatible
    public final E[] toArray(final Class<E> type) {
        return Iterables.toArray(this.getDelegate(), type);
    }
    
    @CanIgnoreReturnValue
    public final <C extends Collection<? super E>> C copyInto(final C collection) {
        Preconditions.checkNotNull(collection);
        final Iterable<E> iterable = this.getDelegate();
        if (iterable instanceof Collection) {
            collection.addAll(Collections2.cast((Iterable<? extends E>)iterable));
        }
        else {
            for (final E item : iterable) {
                collection.add((Object)item);
            }
        }
        return collection;
    }
    
    @Beta
    public final String join(final Joiner joiner) {
        return joiner.join(this);
    }
    
    public final E get(final int position) {
        return Iterables.get(this.getDelegate(), position);
    }
    
    public final Stream<E> stream() {
        return Streams.stream(this.getDelegate());
    }
    
    private static class FromIterableFunction<E> implements Function<Iterable<E>, FluentIterable<E>>
    {
        @Override
        public FluentIterable<E> apply(final Iterable<E> fromObject) {
            return FluentIterable.from(fromObject);
        }
    }
}
