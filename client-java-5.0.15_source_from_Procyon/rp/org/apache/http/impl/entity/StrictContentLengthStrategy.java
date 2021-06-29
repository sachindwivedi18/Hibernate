// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.entity;

import rp.org.apache.http.HttpException;
import rp.org.apache.http.Header;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.entity.ContentLengthStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class StrictContentLengthStrategy implements ContentLengthStrategy
{
    public static final StrictContentLengthStrategy INSTANCE;
    private final int implicitLen;
    
    public StrictContentLengthStrategy(final int implicitLen) {
        this.implicitLen = implicitLen;
    }
    
    public StrictContentLengthStrategy() {
        this(-1);
    }
    
    @Override
    public long determineLength(final HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        final Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader == null) {
            final Header contentLengthHeader = message.getFirstHeader("Content-Length");
            if (contentLengthHeader != null) {
                final String s = contentLengthHeader.getValue();
                try {
                    final long len = Long.parseLong(s);
                    if (len < 0L) {
                        throw new ProtocolException("Negative content length: " + s);
                    }
                    return len;
                }
                catch (NumberFormatException e) {
                    throw new ProtocolException("Invalid content length: " + s);
                }
            }
            return this.implicitLen;
        }
        final String s2 = transferEncodingHeader.getValue();
        if ("chunked".equalsIgnoreCase(s2)) {
            if (message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
            }
            return -2L;
        }
        else {
            if ("identity".equalsIgnoreCase(s2)) {
                return -1L;
            }
            throw new ProtocolException("Unsupported transfer encoding: " + s2);
        }
    }
    
    static {
        INSTANCE = new StrictContentLengthStrategy();
    }
}
