// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.io;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public interface LineProcessor<T>
{
    @CanIgnoreReturnValue
    boolean processLine(final String p0) throws IOException;
    
    T getResult();
}
