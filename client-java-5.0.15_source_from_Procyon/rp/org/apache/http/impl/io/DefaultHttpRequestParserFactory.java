// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.impl.DefaultHttpRequestFactory;
import rp.org.apache.http.message.BasicLineParser;
import rp.org.apache.http.HttpRequestFactory;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageParserFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpRequestParserFactory implements HttpMessageParserFactory<HttpRequest>
{
    public static final DefaultHttpRequestParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpRequestFactory requestFactory;
    
    public DefaultHttpRequestParserFactory(final LineParser lineParser, final HttpRequestFactory requestFactory) {
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.requestFactory = ((requestFactory != null) ? requestFactory : DefaultHttpRequestFactory.INSTANCE);
    }
    
    public DefaultHttpRequestParserFactory() {
        this(null, null);
    }
    
    @Override
    public HttpMessageParser<HttpRequest> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return new DefaultHttpRequestParser(buffer, this.lineParser, this.requestFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestParserFactory();
    }
}
