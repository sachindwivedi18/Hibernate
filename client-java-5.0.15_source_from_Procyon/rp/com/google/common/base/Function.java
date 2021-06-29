// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Function<F, T> extends java.util.function.Function<F, T>
{
    @CanIgnoreReturnValue
    T apply(final F p0);
    
    boolean equals(final Object p0);
}
