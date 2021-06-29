// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public class UncheckedTimeoutException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    public UncheckedTimeoutException() {
    }
    
    public UncheckedTimeoutException(final String message) {
        super(message);
    }
    
    public UncheckedTimeoutException(final Throwable cause) {
        super(cause);
    }
    
    public UncheckedTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
