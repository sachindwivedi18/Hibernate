// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.StatusLine;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.message.ParserCursor;
import rp.org.apache.http.NoHttpResponseException;
import rp.org.apache.http.util.Args;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.HttpResponseFactory;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.impl.io.AbstractMessageParser;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class DefaultResponseParser extends AbstractMessageParser<HttpMessage>
{
    private final Log log;
    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;
    private final int maxGarbageLines;
    
    public DefaultResponseParser(final SessionInputBuffer buffer, final LineParser parser, final HttpResponseFactory responseFactory, final HttpParams params) {
        super(buffer, parser, params);
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(responseFactory, "Response factory");
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
        this.maxGarbageLines = this.getMaxGarbageLines(params);
    }
    
    protected int getMaxGarbageLines(final HttpParams params) {
        return params.getIntParameter("http.connection.max-status-line-garbage", Integer.MAX_VALUE);
    }
    
    @Override
    protected HttpMessage parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException {
        int count = 0;
        ParserCursor cursor = null;
        while (true) {
            this.lineBuf.clear();
            final int i = sessionBuffer.readLine(this.lineBuf);
            if (i == -1 && count == 0) {
                throw new NoHttpResponseException("The target server failed to respond");
            }
            cursor = new ParserCursor(0, this.lineBuf.length());
            if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
                final StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
                return this.responseFactory.newHttpResponse(statusline, null);
            }
            if (i == -1 || count >= this.maxGarbageLines) {
                throw new ProtocolException("The server failed to respond with a valid HTTP response");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Garbage in response: " + this.lineBuf.toString());
            }
            ++count;
        }
    }
}
