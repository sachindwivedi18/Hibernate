// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.reflect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.Beta;
import java.util.Map;

@Beta
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B>
{
     <T extends B> T getInstance(final Class<T> p0);
    
     <T extends B> T getInstance(final TypeToken<T> p0);
    
    @CanIgnoreReturnValue
     <T extends B> T putInstance(final Class<T> p0, final T p1);
    
    @CanIgnoreReturnValue
     <T extends B> T putInstance(final TypeToken<T> p0, final T p1);
}
