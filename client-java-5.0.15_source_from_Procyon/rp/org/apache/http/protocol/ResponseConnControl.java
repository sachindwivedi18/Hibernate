// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.Header;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseConnControl implements HttpResponseInterceptor
{
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        final HttpCoreContext corecontext = HttpCoreContext.adapt(context);
        final int status = response.getStatusLine().getStatusCode();
        if (status == 400 || status == 408 || status == 411 || status == 413 || status == 414 || status == 503 || status == 501) {
            response.setHeader("Connection", "Close");
            return;
        }
        final Header explicit = response.getFirstHeader("Connection");
        if (explicit != null && "Close".equalsIgnoreCase(explicit.getValue())) {
            return;
        }
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
            if (entity.getContentLength() < 0L && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
                response.setHeader("Connection", "Close");
                return;
            }
        }
        final HttpRequest request = corecontext.getRequest();
        if (request != null) {
            final Header header = request.getFirstHeader("Connection");
            if (header != null) {
                response.setHeader("Connection", header.getValue());
            }
            else if (request.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                response.setHeader("Connection", "Close");
            }
        }
    }
}
