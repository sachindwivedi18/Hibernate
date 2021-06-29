// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.base.Equivalence;
import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.base.Function;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public final class Interners
{
    private Interners() {
    }
    
    public static InternerBuilder newBuilder() {
        return new InternerBuilder();
    }
    
    public static <E> Interner<E> newStrongInterner() {
        return newBuilder().strong().build();
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public static <E> Interner<E> newWeakInterner() {
        return newBuilder().weak().build();
    }
    
    public static <E> Function<E, E> asFunction(final Interner<E> interner) {
        return new InternerFunction<E>(Preconditions.checkNotNull(interner));
    }
    
    public static class InternerBuilder
    {
        private final MapMaker mapMaker;
        private boolean strong;
        
        private InternerBuilder() {
            this.mapMaker = new MapMaker();
            this.strong = true;
        }
        
        public InternerBuilder strong() {
            this.strong = true;
            return this;
        }
        
        @GwtIncompatible("java.lang.ref.WeakReference")
        public InternerBuilder weak() {
            this.strong = false;
            return this;
        }
        
        public InternerBuilder concurrencyLevel(final int concurrencyLevel) {
            this.mapMaker.concurrencyLevel(concurrencyLevel);
            return this;
        }
        
        public <E> Interner<E> build() {
            if (!this.strong) {
                this.mapMaker.weakKeys();
            }
            return new InternerImpl<E>(this.mapMaker);
        }
    }
    
    @VisibleForTesting
    static final class InternerImpl<E> implements Interner<E>
    {
        @VisibleForTesting
        final MapMakerInternalMap<E, MapMaker.Dummy, ?, ?> map;
        
        private InternerImpl(final MapMaker mapMaker) {
            this.map = MapMakerInternalMap.createWithDummyValues(mapMaker.keyEquivalence(Equivalence.equals()));
        }
        
        @Override
        public E intern(final E sample) {
            while (true) {
                final MapMakerInternalMap.InternalEntry<E, MapMaker.Dummy, ?> entry = (MapMakerInternalMap.InternalEntry<E, MapMaker.Dummy, ?>)this.map.getEntry(sample);
                if (entry != null) {
                    final E canonical = entry.getKey();
                    if (canonical != null) {
                        return canonical;
                    }
                }
                final MapMaker.Dummy sneaky = this.map.putIfAbsent(sample, MapMaker.Dummy.VALUE);
                if (sneaky == null) {
                    return sample;
                }
            }
        }
    }
    
    private static class InternerFunction<E> implements Function<E, E>
    {
        private final Interner<E> interner;
        
        public InternerFunction(final Interner<E> interner) {
            this.interner = interner;
        }
        
        @Override
        public E apply(final E input) {
            return this.interner.intern(input);
        }
        
        @Override
        public int hashCode() {
            return this.interner.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other instanceof InternerFunction) {
                final InternerFunction<?> that = (InternerFunction<?>)other;
                return this.interner.equals(that.interner);
            }
            return false;
        }
    }
}
