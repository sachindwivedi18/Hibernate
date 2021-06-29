// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B>
{
    @CanIgnoreReturnValue
     <T extends B> T getInstance(final Class<T> p0);
    
    @CanIgnoreReturnValue
     <T extends B> T putInstance(final Class<T> p0, final T p1);
}
