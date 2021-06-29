// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public interface Interner<E>
{
    @CanIgnoreReturnValue
    E intern(final E p0);
}
