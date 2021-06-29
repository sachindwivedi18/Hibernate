// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import rp.com.google.common.collect.ImmutableCollection;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class CombinedFuture<V> extends AggregateFuture<Object, V>
{
    CombinedFuture(final ImmutableCollection<? extends ListenableFuture<?>> futures, final boolean allMustSucceed, final Executor listenerExecutor, final AsyncCallable<V> callable) {
        this.init(new CombinedFutureRunningState(futures, allMustSucceed, new AsyncCallableInterruptibleTask(callable, listenerExecutor)));
    }
    
    CombinedFuture(final ImmutableCollection<? extends ListenableFuture<?>> futures, final boolean allMustSucceed, final Executor listenerExecutor, final Callable<V> callable) {
        this.init(new CombinedFutureRunningState(futures, allMustSucceed, new CallableInterruptibleTask(callable, listenerExecutor)));
    }
    
    private final class CombinedFutureRunningState extends RunningState
    {
        private CombinedFutureInterruptibleTask task;
        
        CombinedFutureRunningState(final ImmutableCollection<? extends ListenableFuture<?>> futures, final boolean allMustSucceed, final CombinedFutureInterruptibleTask task) {
            super((ImmutableCollection<? extends ListenableFuture<? extends InputT>>)futures, allMustSucceed, false);
            this.task = task;
        }
        
        @Override
        void collectOneValue(final boolean allMustSucceed, final int index, final Object returnValue) {
        }
        
        @Override
        void handleAllCompleted() {
            final CombinedFutureInterruptibleTask localTask = this.task;
            if (localTask != null) {
                localTask.execute();
            }
            else {
                Preconditions.checkState(CombinedFuture.this.isDone());
            }
        }
        
        @Override
        void releaseResourcesAfterFailure() {
            super.releaseResourcesAfterFailure();
            this.task = null;
        }
        
        @Override
        void interruptTask() {
            final CombinedFutureInterruptibleTask localTask = this.task;
            if (localTask != null) {
                localTask.interruptTask();
            }
        }
    }
    
    private abstract class CombinedFutureInterruptibleTask<T> extends InterruptibleTask<T>
    {
        private final Executor listenerExecutor;
        boolean thrownByExecute;
        
        public CombinedFutureInterruptibleTask(final Executor listenerExecutor) {
            this.thrownByExecute = true;
            this.listenerExecutor = Preconditions.checkNotNull(listenerExecutor);
        }
        
        @Override
        final boolean isDone() {
            return CombinedFuture.this.isDone();
        }
        
        final void execute() {
            try {
                this.listenerExecutor.execute(this);
            }
            catch (RejectedExecutionException e) {
                if (this.thrownByExecute) {
                    CombinedFuture.this.setException(e);
                }
            }
        }
        
        @Override
        final void afterRanInterruptibly(final T result, final Throwable error) {
            if (error != null) {
                if (error instanceof ExecutionException) {
                    CombinedFuture.this.setException(error.getCause());
                }
                else if (error instanceof CancellationException) {
                    CombinedFuture.this.cancel(false);
                }
                else {
                    CombinedFuture.this.setException(error);
                }
            }
            else {
                this.setValue(result);
            }
        }
        
        abstract void setValue(final T p0);
    }
    
    private final class AsyncCallableInterruptibleTask extends CombinedFutureInterruptibleTask<ListenableFuture<V>>
    {
        private final AsyncCallable<V> callable;
        
        public AsyncCallableInterruptibleTask(final AsyncCallable<V> callable, final Executor listenerExecutor) {
            super(listenerExecutor);
            this.callable = Preconditions.checkNotNull(callable);
        }
        
        @Override
        ListenableFuture<V> runInterruptibly() throws Exception {
            this.thrownByExecute = false;
            final ListenableFuture<V> result = this.callable.call();
            return Preconditions.checkNotNull(result, (Object)"AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)?");
        }
        
        @Override
        void setValue(final ListenableFuture<V> value) {
            CombinedFuture.this.setFuture(value);
        }
        
        @Override
        String toPendingString() {
            return this.callable.toString();
        }
    }
    
    private final class CallableInterruptibleTask extends CombinedFutureInterruptibleTask<V>
    {
        private final Callable<V> callable;
        
        public CallableInterruptibleTask(final Callable<V> callable, final Executor listenerExecutor) {
            super(listenerExecutor);
            this.callable = Preconditions.checkNotNull(callable);
        }
        
        @Override
        V runInterruptibly() throws Exception {
            this.thrownByExecute = false;
            return this.callable.call();
        }
        
        @Override
        void setValue(final V value) {
            CombinedFuture.this.set(value);
        }
        
        @Override
        String toPendingString() {
            return this.callable.toString();
        }
    }
}
