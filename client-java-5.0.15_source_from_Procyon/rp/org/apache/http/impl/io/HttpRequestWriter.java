// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.HttpMessage;
import java.io.IOException;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.HttpRequest;

@Deprecated
public class HttpRequestWriter extends AbstractMessageWriter<HttpRequest>
{
    public HttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
        super(buffer, formatter, params);
    }
    
    @Override
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
