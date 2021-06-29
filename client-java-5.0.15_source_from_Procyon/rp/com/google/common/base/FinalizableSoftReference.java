// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import java.lang.ref.ReferenceQueue;
import rp.com.google.common.annotations.GwtIncompatible;
import java.lang.ref.SoftReference;

@GwtIncompatible
public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference
{
    protected FinalizableSoftReference(final T referent, final FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}
