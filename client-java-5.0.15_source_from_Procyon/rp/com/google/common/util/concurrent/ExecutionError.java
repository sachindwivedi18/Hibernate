// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class ExecutionError extends Error
{
    private static final long serialVersionUID = 0L;
    
    protected ExecutionError() {
    }
    
    protected ExecutionError(final String message) {
        super(message);
    }
    
    public ExecutionError(final String message, final Error cause) {
        super(message, cause);
    }
    
    public ExecutionError(final Error cause) {
        super(cause);
    }
}
