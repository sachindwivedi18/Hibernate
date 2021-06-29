// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Deprecated
@Beta
@GwtIncompatible
public abstract class ForwardingCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture<V> implements CheckedFuture<V, X>
{
    @CanIgnoreReturnValue
    @Override
    public V checkedGet() throws X, Exception {
        return this.delegate().checkedGet();
    }
    
    @CanIgnoreReturnValue
    @Override
    public V checkedGet(final long timeout, final TimeUnit unit) throws TimeoutException, X, Exception {
        return this.delegate().checkedGet(timeout, unit);
    }
    
    @Override
    protected abstract CheckedFuture<V, X> delegate();
    
    @Deprecated
    @Beta
    public abstract static class SimpleForwardingCheckedFuture<V, X extends Exception> extends ForwardingCheckedFuture<V, X>
    {
        private final CheckedFuture<V, X> delegate;
        
        protected SimpleForwardingCheckedFuture(final CheckedFuture<V, X> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }
        
        @Override
        protected final CheckedFuture<V, X> delegate() {
            return this.delegate;
        }
    }
}
