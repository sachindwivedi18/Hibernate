// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
@GwtCompatible
public interface ListenableScheduledFuture<V> extends ScheduledFuture<V>, ListenableFuture<V>
{
}
