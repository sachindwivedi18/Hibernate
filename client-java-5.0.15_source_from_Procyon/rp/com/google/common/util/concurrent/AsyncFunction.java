// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface AsyncFunction<I, O>
{
    ListenableFuture<O> apply(final I p0) throws Exception;
}
