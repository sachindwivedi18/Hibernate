// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.entity;

import rp.org.apache.http.HttpEntity;
import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.impl.io.ContentLengthOutputStream;
import rp.org.apache.http.impl.io.IdentityOutputStream;
import rp.org.apache.http.impl.io.ChunkedOutputStream;
import java.io.OutputStream;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class EntitySerializer
{
    private final ContentLengthStrategy lenStrategy;
    
    public EntitySerializer(final ContentLengthStrategy lenStrategy) {
        this.lenStrategy = Args.notNull(lenStrategy, "Content length strategy");
    }
    
    protected OutputStream doSerialize(final SessionOutputBuffer outbuffer, final HttpMessage message) throws HttpException, IOException {
        final long len = this.lenStrategy.determineLength(message);
        if (len == -2L) {
            return new ChunkedOutputStream(outbuffer);
        }
        if (len == -1L) {
            return new IdentityOutputStream(outbuffer);
        }
        return new ContentLengthOutputStream(outbuffer, len);
    }
    
    public void serialize(final SessionOutputBuffer outbuffer, final HttpMessage message, final HttpEntity entity) throws HttpException, IOException {
        Args.notNull(outbuffer, "Session output buffer");
        Args.notNull(message, "HTTP message");
        Args.notNull(entity, "HTTP entity");
        final OutputStream outStream = this.doSerialize(outbuffer, message);
        entity.writeTo(outStream);
        outStream.close();
    }
}
