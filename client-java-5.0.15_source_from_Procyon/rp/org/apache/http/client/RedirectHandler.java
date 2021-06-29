// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.ProtocolException;
import java.net.URI;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;

@Deprecated
public interface RedirectHandler
{
    boolean isRedirectRequested(final HttpResponse p0, final HttpContext p1);
    
    URI getLocationURI(final HttpResponse p0, final HttpContext p1) throws ProtocolException;
}
