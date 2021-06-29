// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public interface PeekingIterator<E> extends Iterator<E>
{
    E peek();
    
    @CanIgnoreReturnValue
    E next();
    
    void remove();
}
