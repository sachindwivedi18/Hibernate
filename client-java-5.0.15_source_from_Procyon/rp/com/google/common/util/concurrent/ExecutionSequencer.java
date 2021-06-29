// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import rp.com.google.common.annotations.Beta;

@Beta
public final class ExecutionSequencer
{
    private final AtomicReference<ListenableFuture<Object>> ref;
    
    private ExecutionSequencer() {
        this.ref = new AtomicReference<ListenableFuture<Object>>(Futures.immediateFuture((Object)null));
    }
    
    public static ExecutionSequencer create() {
        return new ExecutionSequencer();
    }
    
    public <T> ListenableFuture<T> submit(final Callable<T> callable, final Executor executor) {
        Preconditions.checkNotNull(callable);
        return this.submitAsync((AsyncCallable<T>)new AsyncCallable<T>() {
            @Override
            public ListenableFuture<T> call() throws Exception {
                return Futures.immediateFuture(callable.call());
            }
        }, executor);
    }
    
    public <T> ListenableFuture<T> submitAsync(final AsyncCallable<T> callable, final Executor executor) {
        Preconditions.checkNotNull(callable);
        final AtomicReference<RunningState> runningState = new AtomicReference<RunningState>(RunningState.NOT_RUN);
        final AsyncCallable<T> task = new AsyncCallable<T>() {
            @Override
            public ListenableFuture<T> call() throws Exception {
                if (!runningState.compareAndSet(RunningState.NOT_RUN, RunningState.STARTED)) {
                    return Futures.immediateCancelledFuture();
                }
                return callable.call();
            }
        };
        final SettableFuture<Object> newFuture = SettableFuture.create();
        final ListenableFuture<?> oldFuture = this.ref.getAndSet(newFuture);
        final ListenableFuture<T> taskFuture = Futures.submitAsync(task, new Executor() {
            @Override
            public void execute(final Runnable runnable) {
                oldFuture.addListener(runnable, executor);
            }
        });
        final ListenableFuture<T> outputFuture = Futures.nonCancellationPropagating(taskFuture);
        final Runnable listener = new Runnable() {
            @Override
            public void run() {
                if (taskFuture.isDone() || (outputFuture.isCancelled() && runningState.compareAndSet(RunningState.NOT_RUN, RunningState.CANCELLED))) {
                    newFuture.setFuture(oldFuture);
                }
            }
        };
        outputFuture.addListener(listener, MoreExecutors.directExecutor());
        taskFuture.addListener(listener, MoreExecutors.directExecutor());
        return outputFuture;
    }
    
    enum RunningState
    {
        NOT_RUN, 
        CANCELLED, 
        STARTED;
    }
}
