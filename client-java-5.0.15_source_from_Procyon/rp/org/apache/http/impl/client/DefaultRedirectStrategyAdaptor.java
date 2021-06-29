// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.net.URI;
import rp.org.apache.http.client.methods.HttpGet;
import rp.org.apache.http.client.methods.HttpHead;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.client.RedirectHandler;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.RedirectStrategy;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
class DefaultRedirectStrategyAdaptor implements RedirectStrategy
{
    private final RedirectHandler handler;
    
    public DefaultRedirectStrategyAdaptor(final RedirectHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }
    
    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.handler.getLocationURI(response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        return new HttpGet(uri);
    }
    
    public RedirectHandler getHandler() {
        return this.handler;
    }
}
