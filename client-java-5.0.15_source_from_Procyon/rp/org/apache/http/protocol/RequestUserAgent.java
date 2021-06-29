// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestUserAgent implements HttpRequestInterceptor
{
    private final String userAgent;
    
    public RequestUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }
    
    public RequestUserAgent() {
        this(null);
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.containsHeader("User-Agent")) {
            String s = null;
            final HttpParams params = request.getParams();
            if (params != null) {
                s = (String)params.getParameter("http.useragent");
            }
            if (s == null) {
                s = this.userAgent;
            }
            if (s != null) {
                request.addHeader("User-Agent", s);
            }
        }
    }
}
