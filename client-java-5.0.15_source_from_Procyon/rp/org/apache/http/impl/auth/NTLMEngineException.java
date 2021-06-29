// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.auth;

import rp.org.apache.http.auth.AuthenticationException;

public class NTLMEngineException extends AuthenticationException
{
    private static final long serialVersionUID = 6027981323731768824L;
    
    public NTLMEngineException() {
    }
    
    public NTLMEngineException(final String message) {
        super(message);
    }
    
    public NTLMEngineException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
