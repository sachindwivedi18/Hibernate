// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@FunctionalInterface
@Beta
@GwtCompatible
public interface AsyncCallable<V>
{
    ListenableFuture<V> call() throws Exception;
}
