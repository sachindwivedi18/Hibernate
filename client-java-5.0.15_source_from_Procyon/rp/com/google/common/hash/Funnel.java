// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.hash;

import rp.com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public interface Funnel<T> extends Serializable
{
    void funnel(final T p0, final PrimitiveSink p1);
}
