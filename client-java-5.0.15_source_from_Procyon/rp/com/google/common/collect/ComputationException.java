// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class ComputationException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    public ComputationException(final Throwable cause) {
        super(cause);
    }
}
