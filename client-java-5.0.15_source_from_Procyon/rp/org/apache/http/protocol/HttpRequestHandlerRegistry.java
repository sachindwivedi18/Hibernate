// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.util.Map;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class HttpRequestHandlerRegistry implements HttpRequestHandlerResolver
{
    private final UriPatternMatcher<HttpRequestHandler> matcher;
    
    public HttpRequestHandlerRegistry() {
        this.matcher = new UriPatternMatcher<HttpRequestHandler>();
    }
    
    public void register(final String pattern, final HttpRequestHandler handler) {
        Args.notNull(pattern, "URI request pattern");
        Args.notNull(handler, "Request handler");
        this.matcher.register(pattern, handler);
    }
    
    public void unregister(final String pattern) {
        this.matcher.unregister(pattern);
    }
    
    public void setHandlers(final Map<String, HttpRequestHandler> map) {
        this.matcher.setObjects(map);
    }
    
    public Map<String, HttpRequestHandler> getHandlers() {
        return this.matcher.getObjects();
    }
    
    @Override
    public HttpRequestHandler lookup(final String requestURI) {
        return this.matcher.lookup(requestURI);
    }
}
