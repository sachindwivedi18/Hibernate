// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.Header;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;

public interface ContextAwareAuthScheme extends AuthScheme
{
    Header authenticate(final Credentials p0, final HttpRequest p1, final HttpContext p2) throws AuthenticationException;
}
