// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.base.MoreObjects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;
import java.util.Collection;
import rp.com.google.common.collect.ImmutableCollection;
import rp.com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.base.Function;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public final class Futures extends GwtFuturesCatchingSpecialization
{
    private Futures() {
    }
    
    @Deprecated
    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(final ListenableFuture<V> future, final Function<? super Exception, X> mapper) {
        return new MappingCheckedFuture<V, X>(Preconditions.checkNotNull(future), mapper);
    }
    
    public static <V> ListenableFuture<V> immediateFuture(final V value) {
        if (value == null) {
            final ListenableFuture<V> typedNull = (ListenableFuture<V>)ImmediateFuture.ImmediateSuccessfulFuture.NULL;
            return typedNull;
        }
        return new ImmediateFuture.ImmediateSuccessfulFuture<V>(value);
    }
    
    @Deprecated
    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(final V value) {
        return new ImmediateFuture.ImmediateSuccessfulCheckedFuture<V, X>(value);
    }
    
    public static <V> ListenableFuture<V> immediateFailedFuture(final Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new ImmediateFuture.ImmediateFailedFuture<V>(throwable);
    }
    
    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new ImmediateFuture.ImmediateCancelledFuture<V>();
    }
    
    @Deprecated
    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(final X exception) {
        Preconditions.checkNotNull(exception);
        return new ImmediateFuture.ImmediateFailedCheckedFuture<V, X>(exception);
    }
    
    public static <O> ListenableFuture<O> submitAsync(final AsyncCallable<O> callable, final Executor executor) {
        final TrustedListenableFutureTask<O> task = TrustedListenableFutureTask.create(callable);
        executor.execute(task);
        return task;
    }
    
    @GwtIncompatible
    public static <O> ListenableFuture<O> scheduleAsync(final AsyncCallable<O> callable, final long delay, final TimeUnit timeUnit, final ScheduledExecutorService executorService) {
        final TrustedListenableFutureTask<O> task = TrustedListenableFutureTask.create(callable);
        final Future<?> scheduled = executorService.schedule(task, delay, timeUnit);
        task.addListener(new Runnable() {
            @Override
            public void run() {
                scheduled.cancel(false);
            }
        }, MoreExecutors.directExecutor());
        return task;
    }
    
    @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(final ListenableFuture<? extends V> input, final Class<X> exceptionType, final Function<? super X, ? extends V> fallback, final Executor executor) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback, executor);
    }
    
    @CanIgnoreReturnValue
    @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(final ListenableFuture<? extends V> input, final Class<X> exceptionType, final AsyncFunction<? super X, ? extends V> fallback, final Executor executor) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback, executor);
    }
    
    @GwtIncompatible
    public static <V> ListenableFuture<V> withTimeout(final ListenableFuture<V> delegate, final long time, final TimeUnit unit, final ScheduledExecutorService scheduledExecutor) {
        return TimeoutFuture.create(delegate, time, unit, scheduledExecutor);
    }
    
    public static <I, O> ListenableFuture<O> transformAsync(final ListenableFuture<I> input, final AsyncFunction<? super I, ? extends O> function, final Executor executor) {
        return AbstractTransformFuture.create(input, function, executor);
    }
    
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> input, final Function<? super I, ? extends O> function, final Executor executor) {
        return AbstractTransformFuture.create(input, function, executor);
    }
    
    @GwtIncompatible
    public static <I, O> Future<O> lazyTransform(final Future<I> input, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(function);
        return new Future<O>() {
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return input.cancel(mayInterruptIfRunning);
            }
            
            @Override
            public boolean isCancelled() {
                return input.isCancelled();
            }
            
            @Override
            public boolean isDone() {
                return input.isDone();
            }
            
            @Override
            public O get() throws InterruptedException, ExecutionException {
                return this.applyTransformation(input.get());
            }
            
            @Override
            public O get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return this.applyTransformation(input.get(timeout, unit));
            }
            
            private O applyTransformation(final I input) throws ExecutionException {
                try {
                    return function.apply(input);
                }
                catch (Throwable t) {
                    throw new ExecutionException(t);
                }
            }
        };
    }
    
    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(final ListenableFuture<? extends V>... futures) {
        return (ListenableFuture<List<V>>)new CollectionFuture.ListFuture(ImmutableList.copyOf(futures), true);
    }
    
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(final Iterable<? extends ListenableFuture<? extends V>> futures) {
        return (ListenableFuture<List<V>>)new CollectionFuture.ListFuture((ImmutableCollection<? extends ListenableFuture<?>>)ImmutableList.copyOf((Iterable<?>)futures), true);
    }
    
    @SafeVarargs
    public static <V> FutureCombiner<V> whenAllComplete(final ListenableFuture<? extends V>... futures) {
        return new FutureCombiner<V>(false, (ImmutableList)ImmutableList.copyOf(futures));
    }
    
    public static <V> FutureCombiner<V> whenAllComplete(final Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new FutureCombiner<V>(false, (ImmutableList)ImmutableList.copyOf((Iterable<?>)futures));
    }
    
    @SafeVarargs
    public static <V> FutureCombiner<V> whenAllSucceed(final ListenableFuture<? extends V>... futures) {
        return new FutureCombiner<V>(true, (ImmutableList)ImmutableList.copyOf(futures));
    }
    
    public static <V> FutureCombiner<V> whenAllSucceed(final Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new FutureCombiner<V>(true, (ImmutableList)ImmutableList.copyOf((Iterable<?>)futures));
    }
    
    public static <V> ListenableFuture<V> nonCancellationPropagating(final ListenableFuture<V> future) {
        if (future.isDone()) {
            return future;
        }
        final NonCancellationPropagatingFuture<V> output = new NonCancellationPropagatingFuture<V>(future);
        future.addListener(output, MoreExecutors.directExecutor());
        return output;
    }
    
    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(final ListenableFuture<? extends V>... futures) {
        return (ListenableFuture<List<V>>)new CollectionFuture.ListFuture(ImmutableList.copyOf(futures), false);
    }
    
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(final Iterable<? extends ListenableFuture<? extends V>> futures) {
        return (ListenableFuture<List<V>>)new CollectionFuture.ListFuture((ImmutableCollection<? extends ListenableFuture<?>>)ImmutableList.copyOf((Iterable<?>)futures), false);
    }
    
    @Beta
    public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(final Iterable<? extends ListenableFuture<? extends T>> futures) {
        Collection<ListenableFuture<? extends T>> collection;
        if (futures instanceof Collection) {
            collection = (Collection<ListenableFuture<? extends T>>)(Collection)futures;
        }
        else {
            collection = (Collection<ListenableFuture<? extends T>>)ImmutableList.copyOf((Iterable<?>)futures);
        }
        final ListenableFuture<? extends T>[] copy = collection.toArray(new ListenableFuture[collection.size()]);
        final InCompletionOrderState<T> state = new InCompletionOrderState<T>((ListenableFuture[])copy);
        final ImmutableList.Builder<AbstractFuture<T>> delegatesBuilder = ImmutableList.builder();
        for (int i = 0; i < copy.length; ++i) {
            delegatesBuilder.add(new InCompletionOrderFuture<T>((InCompletionOrderState)state));
        }
        final ImmutableList<AbstractFuture<T>> delegates = delegatesBuilder.build();
        for (int j = 0; j < copy.length; ++j) {
            final int localI = j;
            copy[j].addListener(new Runnable() {
                @Override
                public void run() {
                    state.recordInputCompletion(delegates, localI);
                }
            }, MoreExecutors.directExecutor());
        }
        final ImmutableList<ListenableFuture<T>> delegatesCast = (ImmutableList<ListenableFuture<T>>)delegates;
        return delegatesCast;
    }
    
    public static <V> void addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback, final Executor executor) {
        Preconditions.checkNotNull(callback);
        future.addListener(new CallbackListener<Object>(future, callback), executor);
    }
    
    @CanIgnoreReturnValue
    public static <V> V getDone(final Future<V> future) throws ExecutionException {
        Preconditions.checkState(future.isDone(), "Future was expected to be done: %s", future);
        return Uninterruptibles.getUninterruptibly(future);
    }
    
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> exceptionClass) throws X, Exception {
        return FuturesGetChecked.getChecked(future, exceptionClass);
    }
    
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> exceptionClass, final long timeout, final TimeUnit unit) throws X, Exception {
        return FuturesGetChecked.getChecked(future, exceptionClass, timeout, unit);
    }
    
    @CanIgnoreReturnValue
    public static <V> V getUnchecked(final Future<V> future) {
        Preconditions.checkNotNull(future);
        try {
            return Uninterruptibles.getUninterruptibly(future);
        }
        catch (ExecutionException e) {
            wrapAndThrowUnchecked(e.getCause());
            throw new AssertionError();
        }
    }
    
    private static void wrapAndThrowUnchecked(final Throwable cause) {
        if (cause instanceof Error) {
            throw new ExecutionError((Error)cause);
        }
        throw new UncheckedExecutionException(cause);
    }
    
    @Beta
    @CanIgnoreReturnValue
    @GwtCompatible
    public static final class FutureCombiner<V>
    {
        private final boolean allMustSucceed;
        private final ImmutableList<ListenableFuture<? extends V>> futures;
        
        private FutureCombiner(final boolean allMustSucceed, final ImmutableList<ListenableFuture<? extends V>> futures) {
            this.allMustSucceed = allMustSucceed;
            this.futures = futures;
        }
        
        public <C> ListenableFuture<C> callAsync(final AsyncCallable<C> combiner, final Executor executor) {
            return (ListenableFuture<C>)new CombinedFuture(this.futures, this.allMustSucceed, executor, (AsyncCallable<Object>)combiner);
        }
        
        @CanIgnoreReturnValue
        public <C> ListenableFuture<C> call(final Callable<C> combiner, final Executor executor) {
            return (ListenableFuture<C>)new CombinedFuture(this.futures, this.allMustSucceed, executor, (Callable<Object>)combiner);
        }
        
        public ListenableFuture<?> run(final Runnable combiner, final Executor executor) {
            return this.call((Callable<?>)new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    combiner.run();
                    return null;
                }
            }, executor);
        }
    }
    
    private static final class NonCancellationPropagatingFuture<V> extends TrustedFuture<V> implements Runnable
    {
        private ListenableFuture<V> delegate;
        
        NonCancellationPropagatingFuture(final ListenableFuture<V> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public void run() {
            final ListenableFuture<V> localDelegate = this.delegate;
            if (localDelegate != null) {
                this.setFuture((ListenableFuture<? extends V>)localDelegate);
            }
        }
        
        @Override
        protected String pendingToString() {
            final ListenableFuture<V> localDelegate = this.delegate;
            if (localDelegate != null) {
                return "delegate=[" + localDelegate + "]";
            }
            return null;
        }
        
        @Override
        protected void afterDone() {
            this.delegate = null;
        }
    }
    
    private static final class InCompletionOrderFuture<T> extends AbstractFuture<T>
    {
        private InCompletionOrderState<T> state;
        
        private InCompletionOrderFuture(final InCompletionOrderState<T> state) {
            this.state = state;
        }
        
        @Override
        public boolean cancel(final boolean interruptIfRunning) {
            final InCompletionOrderState<T> localState = this.state;
            if (super.cancel(interruptIfRunning)) {
                ((InCompletionOrderState<Object>)localState).recordOutputCancellation(interruptIfRunning);
                return true;
            }
            return false;
        }
        
        @Override
        protected void afterDone() {
            this.state = null;
        }
        
        @Override
        protected String pendingToString() {
            final InCompletionOrderState<T> localState = this.state;
            if (localState != null) {
                return "inputCount=[" + ((InCompletionOrderState<Object>)localState).inputFutures.length + "], remaining=[" + ((InCompletionOrderState<Object>)localState).incompleteOutputCount.get() + "]";
            }
            return null;
        }
    }
    
    private static final class InCompletionOrderState<T>
    {
        private boolean wasCancelled;
        private boolean shouldInterrupt;
        private final AtomicInteger incompleteOutputCount;
        private final ListenableFuture<? extends T>[] inputFutures;
        private volatile int delegateIndex;
        
        private InCompletionOrderState(final ListenableFuture<? extends T>[] inputFutures) {
            this.wasCancelled = false;
            this.shouldInterrupt = true;
            this.delegateIndex = 0;
            this.inputFutures = inputFutures;
            this.incompleteOutputCount = new AtomicInteger(inputFutures.length);
        }
        
        private void recordOutputCancellation(final boolean interruptIfRunning) {
            this.wasCancelled = true;
            if (!interruptIfRunning) {
                this.shouldInterrupt = false;
            }
            this.recordCompletion();
        }
        
        private void recordInputCompletion(final ImmutableList<AbstractFuture<T>> delegates, final int inputFutureIndex) {
            final ListenableFuture<? extends T> inputFuture = this.inputFutures[inputFutureIndex];
            this.inputFutures[inputFutureIndex] = null;
            for (int i = this.delegateIndex; i < delegates.size(); ++i) {
                if (delegates.get(i).setFuture(inputFuture)) {
                    this.recordCompletion();
                    this.delegateIndex = i + 1;
                    return;
                }
            }
            this.delegateIndex = delegates.size();
        }
        
        private void recordCompletion() {
            if (this.incompleteOutputCount.decrementAndGet() == 0 && this.wasCancelled) {
                for (final ListenableFuture<?> toCancel : this.inputFutures) {
                    if (toCancel != null) {
                        toCancel.cancel(this.shouldInterrupt);
                    }
                }
            }
        }
    }
    
    private static final class CallbackListener<V> implements Runnable
    {
        final Future<V> future;
        final FutureCallback<? super V> callback;
        
        CallbackListener(final Future<V> future, final FutureCallback<? super V> callback) {
            this.future = future;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            V value;
            try {
                value = Futures.getDone(this.future);
            }
            catch (ExecutionException e) {
                this.callback.onFailure(e.getCause());
                return;
            }
            catch (RuntimeException | Error ex) {
                final Throwable t;
                final Throwable e2 = t;
                this.callback.onFailure(e2);
                return;
            }
            this.callback.onSuccess((Object)value);
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).addValue(this.callback).toString();
        }
    }
    
    @GwtIncompatible
    private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X>
    {
        final Function<? super Exception, X> mapper;
        
        MappingCheckedFuture(final ListenableFuture<V> delegate, final Function<? super Exception, X> mapper) {
            super(delegate);
            this.mapper = Preconditions.checkNotNull(mapper);
        }
        
        @Override
        protected X mapException(final Exception e) {
            return this.mapper.apply(e);
        }
    }
}
