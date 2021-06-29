// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestExpectContinue implements HttpRequestInterceptor
{
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.containsHeader("Expect") && request instanceof HttpEntityEnclosingRequest) {
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                final HttpClientContext clientContext = HttpClientContext.adapt(context);
                final RequestConfig config = clientContext.getRequestConfig();
                if (config.isExpectContinueEnabled()) {
                    request.addHeader("Expect", "100-continue");
                }
            }
        }
    }
}
