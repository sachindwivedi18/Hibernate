// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.entity;

import rp.org.apache.http.HttpException;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.entity.ContentLengthStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DisallowIdentityContentLengthStrategy implements ContentLengthStrategy
{
    public static final DisallowIdentityContentLengthStrategy INSTANCE;
    private final ContentLengthStrategy contentLengthStrategy;
    
    public DisallowIdentityContentLengthStrategy(final ContentLengthStrategy contentLengthStrategy) {
        this.contentLengthStrategy = contentLengthStrategy;
    }
    
    @Override
    public long determineLength(final HttpMessage message) throws HttpException {
        final long result = this.contentLengthStrategy.determineLength(message);
        if (result == -1L) {
            throw new ProtocolException("Identity transfer encoding cannot be used");
        }
        return result;
    }
    
    static {
        INSTANCE = new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0));
    }
}
