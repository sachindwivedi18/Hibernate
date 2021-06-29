// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Supplier<T> extends java.util.function.Supplier<T>
{
    @CanIgnoreReturnValue
    T get();
}
