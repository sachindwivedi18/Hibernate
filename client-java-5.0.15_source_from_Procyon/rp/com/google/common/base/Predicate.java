// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Predicate<T> extends java.util.function.Predicate<T>
{
    @CanIgnoreReturnValue
    boolean apply(final T p0);
    
    boolean equals(final Object p0);
    
    default boolean test(final T input) {
        return this.apply(input);
    }
}
