// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class UncheckedExecutionException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    protected UncheckedExecutionException() {
    }
    
    protected UncheckedExecutionException(final String message) {
        super(message);
    }
    
    public UncheckedExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public UncheckedExecutionException(final Throwable cause) {
        super(cause);
    }
}
