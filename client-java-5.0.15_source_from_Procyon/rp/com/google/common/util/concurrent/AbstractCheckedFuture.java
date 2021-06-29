// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Deprecated
@Beta
@GwtIncompatible
public abstract class AbstractCheckedFuture<V, X extends Exception> extends SimpleForwardingListenableFuture<V> implements CheckedFuture<V, X>
{
    protected AbstractCheckedFuture(final ListenableFuture<V> delegate) {
        super(delegate);
    }
    
    protected abstract X mapException(final Exception p0);
    
    @CanIgnoreReturnValue
    @Override
    public V checkedGet() throws X, Exception {
        try {
            return this.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw this.mapException(e);
        }
        catch (CancellationException | ExecutionException ex2) {
            final Exception ex;
            final Exception e2 = ex;
            throw this.mapException(e2);
        }
    }
    
    @CanIgnoreReturnValue
    @Override
    public V checkedGet(final long timeout, final TimeUnit unit) throws TimeoutException, X, Exception {
        try {
            return this.get(timeout, unit);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw this.mapException(e);
        }
        catch (CancellationException | ExecutionException ex2) {
            final Exception ex;
            final Exception e2 = ex;
            throw this.mapException(e2);
        }
    }
}
