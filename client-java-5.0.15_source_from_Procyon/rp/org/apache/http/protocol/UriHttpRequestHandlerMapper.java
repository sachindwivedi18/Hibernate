// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public class UriHttpRequestHandlerMapper implements HttpRequestHandlerMapper
{
    private final UriPatternMatcher<HttpRequestHandler> matcher;
    
    protected UriHttpRequestHandlerMapper(final UriPatternMatcher<HttpRequestHandler> matcher) {
        this.matcher = Args.notNull(matcher, "Pattern matcher");
    }
    
    public UriHttpRequestHandlerMapper() {
        this(new UriPatternMatcher<HttpRequestHandler>());
    }
    
    public void register(final String pattern, final HttpRequestHandler handler) {
        Args.notNull(pattern, "Pattern");
        Args.notNull(handler, "Handler");
        this.matcher.register(pattern, handler);
    }
    
    public void unregister(final String pattern) {
        this.matcher.unregister(pattern);
    }
    
    protected String getRequestPath(final HttpRequest request) {
        String uriPath = request.getRequestLine().getUri();
        int index = uriPath.indexOf(63);
        if (index != -1) {
            uriPath = uriPath.substring(0, index);
        }
        else {
            index = uriPath.indexOf(35);
            if (index != -1) {
                uriPath = uriPath.substring(0, index);
            }
        }
        return uriPath;
    }
    
    @Override
    public HttpRequestHandler lookup(final HttpRequest request) {
        Args.notNull(request, "HTTP request");
        return this.matcher.lookup(this.getRequestPath(request));
    }
}
