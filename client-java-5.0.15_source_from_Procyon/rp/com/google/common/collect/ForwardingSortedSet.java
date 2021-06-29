// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import rp.com.google.common.annotations.Beta;
import java.util.NoSuchElementException;
import java.util.Comparator;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.SortedSet;

@GwtCompatible
public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E>
{
    protected ForwardingSortedSet() {
    }
    
    @Override
    protected abstract SortedSet<E> delegate();
    
    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }
    
    @Override
    public E first() {
        return this.delegate().first();
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        return this.delegate().headSet(toElement);
    }
    
    @Override
    public E last() {
        return this.delegate().last();
    }
    
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        return this.delegate().subSet(fromElement, toElement);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        return this.delegate().tailSet(fromElement);
    }
    
    private int unsafeCompare(final Object o1, final Object o2) {
        final Comparator<? super E> comparator = this.comparator();
        return (comparator == null) ? ((Comparable)o1).compareTo(o2) : comparator.compare((Object)o1, (Object)o2);
    }
    
    @Beta
    @Override
    protected boolean standardContains(final Object object) {
        try {
            final SortedSet<Object> self = (SortedSet<Object>)this;
            final Object ceiling = self.tailSet(object).first();
            return this.unsafeCompare(ceiling, object) == 0;
        }
        catch (ClassCastException | NoSuchElementException | NullPointerException ex2) {
            final RuntimeException ex;
            final RuntimeException e = ex;
            return false;
        }
    }
    
    @Beta
    @Override
    protected boolean standardRemove(final Object object) {
        try {
            final SortedSet<Object> self = (SortedSet<Object>)this;
            final Iterator<Object> iterator = self.tailSet(object).iterator();
            if (iterator.hasNext()) {
                final Object ceiling = iterator.next();
                if (this.unsafeCompare(ceiling, object) == 0) {
                    iterator.remove();
                    return true;
                }
            }
        }
        catch (ClassCastException | NullPointerException ex2) {
            final RuntimeException ex;
            final RuntimeException e = ex;
            return false;
        }
        return false;
    }
    
    @Beta
    protected SortedSet<E> standardSubSet(final E fromElement, final E toElement) {
        return this.tailSet(fromElement).headSet(toElement);
    }
}
