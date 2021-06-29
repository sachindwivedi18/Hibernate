// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.RequestLine;
import rp.org.apache.http.message.ParserCursor;
import rp.org.apache.http.ConnectionClosedException;
import rp.org.apache.http.impl.DefaultHttpRequestFactory;
import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.HttpRequestFactory;
import rp.org.apache.http.HttpRequest;

public class DefaultHttpRequestParser extends AbstractMessageParser<HttpRequest>
{
    private final HttpRequestFactory requestFactory;
    private final CharArrayBuffer lineBuf;
    
    @Deprecated
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpRequestFactory requestFactory, final HttpParams params) {
        super(buffer, lineParser, params);
        this.requestFactory = Args.notNull(requestFactory, "Request factory");
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpRequestFactory requestFactory, final MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.requestFactory = ((requestFactory != null) ? requestFactory : DefaultHttpRequestFactory.INSTANCE);
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }
    
    @Override
    protected HttpRequest parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        final int readLen = sessionBuffer.readLine(this.lineBuf);
        if (readLen == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        final ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
        final RequestLine requestline = this.lineParser.parseRequestLine(this.lineBuf, cursor);
        return this.requestFactory.newHttpRequest(requestline);
    }
}
