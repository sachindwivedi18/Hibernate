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
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestContent implements HttpRequestInterceptor
{
    private final boolean overwrite;
    
    public RequestContent() {
        this(false);
    }
    
    public RequestContent(final boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (request instanceof HttpEntityEnclosingRequest) {
            if (this.overwrite) {
                request.removeHeaders("Transfer-Encoding");
                request.removeHeaders("Content-Length");
            }
            else {
                if (request.containsHeader("Transfer-Encoding")) {
                    throw new ProtocolException("Transfer-encoding header already present");
                }
                if (request.containsHeader("Content-Length")) {
                    throw new ProtocolException("Content-Length header already present");
                }
            }
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity == null) {
                request.addHeader("Content-Length", "0");
                return;
            }
            if (entity.isChunked() || entity.getContentLength() < 0L) {
                if (ver.lessEquals(HttpVersion.HTTP_1_0)) {
                    throw new ProtocolException("Chunked transfer encoding not allowed for " + ver);
                }
                request.addHeader("Transfer-Encoding", "chunked");
            }
            else {
                request.addHeader("Content-Length", Long.toString(entity.getContentLength()));
            }
            if (entity.getContentType() != null && !request.containsHeader("Content-Type")) {
                request.addHeader(entity.getContentType());
            }
            if (entity.getContentEncoding() != null && !request.containsHeader("Content-Encoding")) {
                request.addHeader(entity.getContentEncoding());
            }
        }
    }
}
