// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collector;
import java.util.Iterator;
import rp.com.google.common.base.Preconditions;
import java.util.Comparator;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class Comparators
{
    private Comparators() {
    }
    
    public static <T, S extends T> Comparator<Iterable<S>> lexicographical(final Comparator<T> comparator) {
        return (Comparator<Iterable<S>>)new LexicographicalOrdering((Comparator<? super Object>)Preconditions.checkNotNull(comparator));
    }
    
    public static <T> boolean isInOrder(final Iterable<? extends T> iterable, final Comparator<T> comparator) {
        Preconditions.checkNotNull(comparator);
        final Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T prev = (T)it.next();
            while (it.hasNext()) {
                final T next = (T)it.next();
                if (comparator.compare(prev, next) > 0) {
                    return false;
                }
                prev = next;
            }
        }
        return true;
    }
    
    public static <T> boolean isInStrictOrder(final Iterable<? extends T> iterable, final Comparator<T> comparator) {
        Preconditions.checkNotNull(comparator);
        final Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            T prev = (T)it.next();
            while (it.hasNext()) {
                final T next = (T)it.next();
                if (comparator.compare(prev, next) >= 0) {
                    return false;
                }
                prev = next;
            }
        }
        return true;
    }
    
    public static <T> Collector<T, ?, List<T>> least(final int k, final Comparator<? super T> comparator) {
        CollectPreconditions.checkNonnegative(k, "k");
        Preconditions.checkNotNull(comparator);
        return Collector.of(() -> TopKSelector.least(k, (Comparator<? super Object>)comparator), TopKSelector::offer, TopKSelector::combine, TopKSelector::topK, Collector.Characteristics.UNORDERED);
    }
    
    public static <T> Collector<T, ?, List<T>> greatest(final int k, final Comparator<? super T> comparator) {
        return (Collector<T, ?, List<T>>)least(k, (Comparator<? super Object>)comparator.reversed());
    }
    
    @Beta
    public static <T> Comparator<Optional<T>> emptiesFirst(final Comparator<? super T> valueComparator) {
        Preconditions.checkNotNull(valueComparator);
        return Comparator.comparing(o -> o.orElse(null), Comparator.nullsFirst((Comparator<? super Object>)valueComparator));
    }
    
    @Beta
    public static <T> Comparator<Optional<T>> emptiesLast(final Comparator<? super T> valueComparator) {
        Preconditions.checkNotNull(valueComparator);
        return Comparator.comparing(o -> o.orElse(null), Comparator.nullsLast((Comparator<? super Object>)valueComparator));
    }
}
