// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import rp.com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.Beta;

@Deprecated
@Beta
@CanIgnoreReturnValue
@GwtCompatible
public interface CheckedFuture<V, X extends Exception> extends ListenableFuture<V>
{
    V checkedGet() throws X, Exception;
    
    V checkedGet(final long p0, final TimeUnit p1) throws TimeoutException, X, Exception;
}
