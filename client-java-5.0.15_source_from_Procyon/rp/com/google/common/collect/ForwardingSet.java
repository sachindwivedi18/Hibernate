// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.base.Preconditions;
import java.util.Collection;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingSet<E> extends ForwardingCollection<E> implements Set<E>
{
    protected ForwardingSet() {
    }
    
    @Override
    protected abstract Set<E> delegate();
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.delegate().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    protected boolean standardRemoveAll(final Collection<?> collection) {
        return Sets.removeAllImpl(this, Preconditions.checkNotNull(collection));
    }
    
    protected boolean standardEquals(final Object object) {
        return Sets.equalsImpl(this, object);
    }
    
    protected int standardHashCode() {
        return Sets.hashCodeImpl(this);
    }
}
