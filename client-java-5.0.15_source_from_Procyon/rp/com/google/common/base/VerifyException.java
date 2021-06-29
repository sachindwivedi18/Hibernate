// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class VerifyException extends RuntimeException
{
    public VerifyException() {
    }
    
    public VerifyException(final String message) {
        super(message);
    }
    
    public VerifyException(final Throwable cause) {
        super(cause);
    }
    
    public VerifyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
