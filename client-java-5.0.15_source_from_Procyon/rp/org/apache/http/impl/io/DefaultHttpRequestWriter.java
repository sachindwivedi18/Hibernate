// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.HttpMessage;
import java.io.IOException;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.HttpRequest;

public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest>
{
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer) {
        this(buffer, null);
    }
    
    @Override
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
