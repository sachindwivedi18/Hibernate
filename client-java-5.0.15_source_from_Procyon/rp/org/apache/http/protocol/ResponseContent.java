// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseContent implements HttpResponseInterceptor
{
    private final boolean overwrite;
    
    public ResponseContent() {
        this(false);
    }
    
    public ResponseContent(final boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        if (this.overwrite) {
            response.removeHeaders("Transfer-Encoding");
            response.removeHeaders("Content-Length");
        }
        else {
            if (response.containsHeader("Transfer-Encoding")) {
                throw new ProtocolException("Transfer-encoding header already present");
            }
            if (response.containsHeader("Content-Length")) {
                throw new ProtocolException("Content-Length header already present");
            }
        }
        final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            final long len = entity.getContentLength();
            if (entity.isChunked() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                response.addHeader("Transfer-Encoding", "chunked");
            }
            else if (len >= 0L) {
                response.addHeader("Content-Length", Long.toString(entity.getContentLength()));
            }
            if (entity.getContentType() != null && !response.containsHeader("Content-Type")) {
                response.addHeader(entity.getContentType());
            }
            if (entity.getContentEncoding() != null && !response.containsHeader("Content-Encoding")) {
                response.addHeader(entity.getContentEncoding());
            }
        }
        else {
            final int status = response.getStatusLine().getStatusCode();
            if (status != 204 && status != 304 && status != 205) {
                response.addHeader("Content-Length", "0");
            }
        }
    }
}
