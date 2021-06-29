// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.StatusLine;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.message.ParserCursor;
import rp.org.apache.http.NoHttpResponseException;
import rp.org.apache.http.impl.DefaultHttpResponseFactory;
import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.HttpResponseFactory;
import rp.org.apache.http.HttpResponse;

public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse>
{
    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;
    
    @Deprecated
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpResponseFactory responseFactory, final HttpParams params) {
        super(buffer, lineParser, params);
        this.responseFactory = Args.notNull(responseFactory, "Response factory");
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpResponseFactory responseFactory, final MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.responseFactory = ((responseFactory != null) ? responseFactory : DefaultHttpResponseFactory.INSTANCE);
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }
    
    @Override
    protected HttpResponse parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        final int readLen = sessionBuffer.readLine(this.lineBuf);
        if (readLen == -1) {
            throw new NoHttpResponseException("The target server failed to respond");
        }
        final ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
        final StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
        return this.responseFactory.newHttpResponse(statusline, null);
    }
}
