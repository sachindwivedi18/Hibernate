// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.concurrent.RunnableFuture;

@GwtCompatible
class TrustedListenableFutureTask<V> extends TrustedFuture<V> implements RunnableFuture<V>
{
    private volatile InterruptibleTask<?> task;
    
    static <V> TrustedListenableFutureTask<V> create(final AsyncCallable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }
    
    static <V> TrustedListenableFutureTask<V> create(final Callable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }
    
    static <V> TrustedListenableFutureTask<V> create(final Runnable runnable, final V result) {
        return new TrustedListenableFutureTask<V>(Executors.callable(runnable, result));
    }
    
    TrustedListenableFutureTask(final Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask(callable);
    }
    
    TrustedListenableFutureTask(final AsyncCallable<V> callable) {
        this.task = new TrustedFutureInterruptibleAsyncTask(callable);
    }
    
    @Override
    public void run() {
        final InterruptibleTask localTask = this.task;
        if (localTask != null) {
            localTask.run();
        }
        this.task = null;
    }
    
    @Override
    protected void afterDone() {
        super.afterDone();
        if (this.wasInterrupted()) {
            final InterruptibleTask localTask = this.task;
            if (localTask != null) {
                localTask.interruptTask();
            }
        }
        this.task = null;
    }
    
    @Override
    protected String pendingToString() {
        final InterruptibleTask localTask = this.task;
        if (localTask != null) {
            return "task=[" + localTask + "]";
        }
        return super.pendingToString();
    }
    
    private final class TrustedFutureInterruptibleTask extends InterruptibleTask<V>
    {
        private final Callable<V> callable;
        
        TrustedFutureInterruptibleTask(final Callable<V> callable) {
            this.callable = Preconditions.checkNotNull(callable);
        }
        
        @Override
        final boolean isDone() {
            return TrustedListenableFutureTask.this.isDone();
        }
        
        @Override
        V runInterruptibly() throws Exception {
            return this.callable.call();
        }
        
        @Override
        void afterRanInterruptibly(final V result, final Throwable error) {
            if (error == null) {
                TrustedListenableFutureTask.this.set(result);
            }
            else {
                TrustedListenableFutureTask.this.setException(error);
            }
        }
        
        @Override
        String toPendingString() {
            return this.callable.toString();
        }
    }
    
    private final class TrustedFutureInterruptibleAsyncTask extends InterruptibleTask<ListenableFuture<V>>
    {
        private final AsyncCallable<V> callable;
        
        TrustedFutureInterruptibleAsyncTask(final AsyncCallable<V> callable) {
            this.callable = Preconditions.checkNotNull(callable);
        }
        
        @Override
        final boolean isDone() {
            return TrustedListenableFutureTask.this.isDone();
        }
        
        @Override
        ListenableFuture<V> runInterruptibly() throws Exception {
            return Preconditions.checkNotNull(this.callable.call(), (Object)"AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)?");
        }
        
        @Override
        void afterRanInterruptibly(final ListenableFuture<V> result, final Throwable error) {
            if (error == null) {
                TrustedListenableFutureTask.this.setFuture(result);
            }
            else {
                TrustedListenableFutureTask.this.setException(error);
            }
        }
        
        @Override
        String toPendingString() {
            return this.callable.toString();
        }
    }
}
