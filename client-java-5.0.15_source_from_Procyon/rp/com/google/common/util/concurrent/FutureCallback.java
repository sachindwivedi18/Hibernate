// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface FutureCallback<V>
{
    void onSuccess(final V p0);
    
    void onFailure(final Throwable p0);
}
