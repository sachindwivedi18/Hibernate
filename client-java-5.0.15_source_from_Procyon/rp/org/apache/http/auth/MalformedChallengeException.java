// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.ProtocolException;

public class MalformedChallengeException extends ProtocolException
{
    private static final long serialVersionUID = 814586927989932284L;
    
    public MalformedChallengeException() {
    }
    
    public MalformedChallengeException(final String message) {
        super(message);
    }
    
    public MalformedChallengeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
