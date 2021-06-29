// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.HttpConnection;
import java.io.IOException;
import java.net.Socket;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParserFactory;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageWriterFactory;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.config.ConnectionConfig;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpConnectionFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultBHttpClientConnectionFactory implements HttpConnectionFactory<DefaultBHttpClientConnection>
{
    public static final DefaultBHttpClientConnectionFactory INSTANCE;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final HttpMessageParserFactory<HttpResponse> responseParserFactory;
    
    public DefaultBHttpClientConnectionFactory(final ConnectionConfig cconfig, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        this.cconfig = ((cconfig != null) ? cconfig : ConnectionConfig.DEFAULT);
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.requestWriterFactory = requestWriterFactory;
        this.responseParserFactory = responseParserFactory;
    }
    
    public DefaultBHttpClientConnectionFactory(final ConnectionConfig cconfig, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        this(cconfig, null, null, requestWriterFactory, responseParserFactory);
    }
    
    public DefaultBHttpClientConnectionFactory(final ConnectionConfig cconfig) {
        this(cconfig, null, null, null, null);
    }
    
    public DefaultBHttpClientConnectionFactory() {
        this(null, null, null, null, null);
    }
    
    @Override
    public DefaultBHttpClientConnection createConnection(final Socket socket) throws IOException {
        final DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
        conn.bind(socket);
        return conn;
    }
    
    static {
        INSTANCE = new DefaultBHttpClientConnectionFactory();
    }
}
