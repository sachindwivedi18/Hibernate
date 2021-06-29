// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import java.util.Iterator;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.Header;
import java.util.Collection;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RequestDefaultHeaders implements HttpRequestInterceptor
{
    private final Collection<? extends Header> defaultHeaders;
    
    public RequestDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }
    
    public RequestDefaultHeaders() {
        this(null);
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            return;
        }
        Collection<? extends Header> defHeaders = (Collection<? extends Header>)request.getParams().getParameter("http.default-headers");
        if (defHeaders == null) {
            defHeaders = this.defaultHeaders;
        }
        if (defHeaders != null) {
            for (final Header defHeader : defHeaders) {
                request.addHeader(defHeader);
            }
        }
    }
}
