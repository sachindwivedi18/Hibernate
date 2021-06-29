// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import java.util.List;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestAcceptEncoding implements HttpRequestInterceptor
{
    private final String acceptEncoding;
    
    public RequestAcceptEncoding(final List<String> encodings) {
        if (encodings != null && !encodings.isEmpty()) {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < encodings.size(); ++i) {
                if (i > 0) {
                    buf.append(",");
                }
                buf.append(encodings.get(i));
            }
            this.acceptEncoding = buf.toString();
        }
        else {
            this.acceptEncoding = "gzip,deflate";
        }
    }
    
    public RequestAcceptEncoding() {
        this(null);
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig requestConfig = clientContext.getRequestConfig();
        if (!request.containsHeader("Accept-Encoding") && requestConfig.isContentCompressionEnabled()) {
            request.addHeader("Accept-Encoding", this.acceptEncoding);
        }
    }
}
