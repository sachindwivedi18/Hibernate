// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.impl.DefaultHttpResponseFactory;
import rp.org.apache.http.message.BasicLineParser;
import rp.org.apache.http.HttpResponseFactory;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParserFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseParserFactory implements HttpMessageParserFactory<HttpResponse>
{
    public static final DefaultHttpResponseParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpResponseFactory responseFactory;
    
    public DefaultHttpResponseParserFactory(final LineParser lineParser, final HttpResponseFactory responseFactory) {
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.responseFactory = ((responseFactory != null) ? responseFactory : DefaultHttpResponseFactory.INSTANCE);
    }
    
    public DefaultHttpResponseParserFactory() {
        this(null, null);
    }
    
    @Override
    public HttpMessageParser<HttpResponse> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return new DefaultHttpResponseParser(buffer, this.lineParser, this.responseFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpResponseParserFactory();
    }
}
