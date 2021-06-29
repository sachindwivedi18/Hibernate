// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.ProtocolException;

public class NonRepeatableRequestException extends ProtocolException
{
    private static final long serialVersionUID = 82685265288806048L;
    
    public NonRepeatableRequestException() {
    }
    
    public NonRepeatableRequestException(final String message) {
        super(message);
    }
    
    public NonRepeatableRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
