// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.auth.AuthenticationException;
import rp.org.apache.http.auth.AuthScheme;
import rp.org.apache.http.auth.MalformedChallengeException;
import rp.org.apache.http.Header;
import java.util.Map;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;

@Deprecated
public interface AuthenticationHandler
{
    boolean isAuthenticationRequested(final HttpResponse p0, final HttpContext p1);
    
    Map<String, Header> getChallenges(final HttpResponse p0, final HttpContext p1) throws MalformedChallengeException;
    
    AuthScheme selectScheme(final Map<String, Header> p0, final HttpResponse p1, final HttpContext p2) throws AuthenticationException;
}
