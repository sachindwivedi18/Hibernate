// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.entity;

import rp.org.apache.http.HttpEntity;
import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.Header;
import rp.org.apache.http.impl.io.ContentLengthInputStream;
import rp.org.apache.http.impl.io.IdentityInputStream;
import java.io.InputStream;
import rp.org.apache.http.impl.io.ChunkedInputStream;
import rp.org.apache.http.entity.BasicHttpEntity;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class EntityDeserializer
{
    private final ContentLengthStrategy lenStrategy;
    
    public EntityDeserializer(final ContentLengthStrategy lenStrategy) {
        this.lenStrategy = Args.notNull(lenStrategy, "Content length strategy");
    }
    
    protected BasicHttpEntity doDeserialize(final SessionInputBuffer inBuffer, final HttpMessage message) throws HttpException, IOException {
        final BasicHttpEntity entity = new BasicHttpEntity();
        final long len = this.lenStrategy.determineLength(message);
        if (len == -2L) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
            entity.setContent(new ChunkedInputStream(inBuffer));
        }
        else if (len == -1L) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
            entity.setContent(new IdentityInputStream(inBuffer));
        }
        else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(new ContentLengthInputStream(inBuffer, len));
        }
        final Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        final Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }
    
    public HttpEntity deserialize(final SessionInputBuffer inBuffer, final HttpMessage message) throws HttpException, IOException {
        Args.notNull(inBuffer, "Session input buffer");
        Args.notNull(message, "HTTP message");
        return this.doDeserialize(inBuffer, message);
    }
}
