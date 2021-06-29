// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import rp.com.google.common.base.Preconditions;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
final class TimeoutFuture<V> extends TrustedFuture<V>
{
    private ListenableFuture<V> delegateRef;
    private Future<?> timer;
    
    static <V> ListenableFuture<V> create(final ListenableFuture<V> delegate, final long time, final TimeUnit unit, final ScheduledExecutorService scheduledExecutor) {
        final TimeoutFuture<V> result = new TimeoutFuture<V>(delegate);
        final Fire<V> fire = new Fire<V>(result);
        result.timer = scheduledExecutor.schedule(fire, time, unit);
        delegate.addListener(fire, MoreExecutors.directExecutor());
        return result;
    }
    
    private TimeoutFuture(final ListenableFuture<V> delegate) {
        this.delegateRef = Preconditions.checkNotNull(delegate);
    }
    
    @Override
    protected String pendingToString() {
        final ListenableFuture<? extends V> localInputFuture = (ListenableFuture<? extends V>)this.delegateRef;
        if (localInputFuture != null) {
            return "inputFuture=[" + localInputFuture + "]";
        }
        return null;
    }
    
    @Override
    protected void afterDone() {
        this.maybePropagateCancellationTo(this.delegateRef);
        final Future<?> localTimer = this.timer;
        if (localTimer != null) {
            localTimer.cancel(false);
        }
        this.delegateRef = null;
        this.timer = null;
    }
    
    private static final class Fire<V> implements Runnable
    {
        TimeoutFuture<V> timeoutFutureRef;
        
        Fire(final TimeoutFuture<V> timeoutFuture) {
            this.timeoutFutureRef = timeoutFuture;
        }
        
        @Override
        public void run() {
            final TimeoutFuture<V> timeoutFuture = this.timeoutFutureRef;
            if (timeoutFuture == null) {
                return;
            }
            final ListenableFuture<V> delegate = (ListenableFuture<V>)((TimeoutFuture<Object>)timeoutFuture).delegateRef;
            if (delegate == null) {
                return;
            }
            this.timeoutFutureRef = null;
            if (delegate.isDone()) {
                timeoutFuture.setFuture((ListenableFuture<?>)delegate);
            }
            else {
                try {
                    timeoutFuture.setException(new TimeoutException("Future timed out: " + delegate));
                }
                finally {
                    delegate.cancel(true);
                }
            }
        }
    }
}
