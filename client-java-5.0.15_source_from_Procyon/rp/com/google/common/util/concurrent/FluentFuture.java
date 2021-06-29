// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import rp.com.google.common.base.Function;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class FluentFuture<V> extends GwtFluentFutureCatchingSpecialization<V>
{
    FluentFuture() {
    }
    
    public static <V> FluentFuture<V> from(final ListenableFuture<V> future) {
        return (future instanceof FluentFuture) ? ((FluentFuture)future) : new ForwardingFluentFuture<V>(future);
    }
    
    @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public final <X extends Throwable> FluentFuture<V> catching(final Class<X> exceptionType, final Function<? super X, ? extends V> fallback, final Executor executor) {
        return (FluentFuture)Futures.catching((ListenableFuture<? extends V>)this, exceptionType, fallback, executor);
    }
    
    @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public final <X extends Throwable> FluentFuture<V> catchingAsync(final Class<X> exceptionType, final AsyncFunction<? super X, ? extends V> fallback, final Executor executor) {
        return (FluentFuture)Futures.catchingAsync((ListenableFuture<? extends V>)this, exceptionType, fallback, executor);
    }
    
    @GwtIncompatible
    public final FluentFuture<V> withTimeout(final long timeout, final TimeUnit unit, final ScheduledExecutorService scheduledExecutor) {
        return (FluentFuture)Futures.withTimeout((ListenableFuture<V>)this, timeout, unit, scheduledExecutor);
    }
    
    public final <T> FluentFuture<T> transformAsync(final AsyncFunction<? super V, T> function, final Executor executor) {
        return (FluentFuture<T>)(FluentFuture)Futures.transformAsync((ListenableFuture<Object>)this, (AsyncFunction<? super Object, ? extends T>)function, executor);
    }
    
    public final <T> FluentFuture<T> transform(final Function<? super V, T> function, final Executor executor) {
        return (FluentFuture<T>)(FluentFuture)Futures.transform((ListenableFuture<Object>)this, (Function<? super Object, ? extends T>)function, executor);
    }
    
    public final void addCallback(final FutureCallback<? super V> callback, final Executor executor) {
        Futures.addCallback((ListenableFuture<Object>)this, (FutureCallback<? super Object>)callback, executor);
    }
}
