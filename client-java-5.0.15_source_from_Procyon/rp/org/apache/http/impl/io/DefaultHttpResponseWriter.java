// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.HttpMessage;
import java.io.IOException;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.HttpResponse;

public class DefaultHttpResponseWriter extends AbstractMessageWriter<HttpResponse>
{
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer) {
        super(buffer, null);
    }
    
    @Override
    protected void writeHeadLine(final HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
