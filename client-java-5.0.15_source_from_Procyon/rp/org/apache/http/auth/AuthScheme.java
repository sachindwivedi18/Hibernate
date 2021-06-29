// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.Header;

public interface AuthScheme
{
    void processChallenge(final Header p0) throws MalformedChallengeException;
    
    String getSchemeName();
    
    String getParameter(final String p0);
    
    String getRealm();
    
    boolean isConnectionBased();
    
    boolean isComplete();
    
    @Deprecated
    Header authenticate(final Credentials p0, final HttpRequest p1) throws AuthenticationException;
}
