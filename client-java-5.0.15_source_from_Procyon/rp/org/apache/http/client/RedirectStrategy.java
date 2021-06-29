// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;

public interface RedirectStrategy
{
    boolean isRedirected(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws ProtocolException;
    
    HttpUriRequest getRedirect(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws ProtocolException;
}
