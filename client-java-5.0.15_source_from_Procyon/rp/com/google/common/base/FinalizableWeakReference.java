// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import java.lang.ref.ReferenceQueue;
import rp.com.google.common.annotations.GwtIncompatible;
import java.lang.ref.WeakReference;

@GwtIncompatible
public abstract class FinalizableWeakReference<T> extends WeakReference<T> implements FinalizableReference
{
    protected FinalizableWeakReference(final T referent, final FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}
