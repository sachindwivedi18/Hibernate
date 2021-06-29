// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.ProtocolException;

public class AuthenticationException extends ProtocolException
{
    private static final long serialVersionUID = -6794031905674764776L;
    
    public AuthenticationException() {
    }
    
    public AuthenticationException(final String message) {
        super(message);
    }
    
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
