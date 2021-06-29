// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    static boolean isInstanceOfThrowableClass(final Throwable t, final Class<? extends Throwable> expectedClass) {
        return expectedClass.isInstance(t);
    }
    
    private Platform() {
    }
}
