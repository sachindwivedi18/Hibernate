// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import java.io.OutputStream;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.HttpException;
import java.io.IOException;
import java.net.Socket;
import rp.org.apache.http.impl.io.DefaultHttpResponseWriterFactory;
import rp.org.apache.http.impl.io.DefaultHttpRequestParserFactory;
import rp.org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import rp.org.apache.http.io.HttpMessageWriterFactory;
import rp.org.apache.http.io.HttpMessageParserFactory;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.config.MessageConstraints;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageWriter;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.HttpServerConnection;

public class DefaultBHttpServerConnection extends BHttpConnectionBase implements HttpServerConnection
{
    private final HttpMessageParser<HttpRequest> requestParser;
    private final HttpMessageWriter<HttpResponse> responseWriter;
    
    public DefaultBHttpServerConnection(final int bufferSize, final int fragmentSizeHint, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageParserFactory<HttpRequest> requestParserFactory, final HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, (incomingContentStrategy != null) ? incomingContentStrategy : DisallowIdentityContentLengthStrategy.INSTANCE, outgoingContentStrategy);
        this.requestParser = ((requestParserFactory != null) ? requestParserFactory : DefaultHttpRequestParserFactory.INSTANCE).create(this.getSessionInputBuffer(), constraints);
        this.responseWriter = ((responseWriterFactory != null) ? responseWriterFactory : DefaultHttpResponseWriterFactory.INSTANCE).create(this.getSessionOutputBuffer());
    }
    
    public DefaultBHttpServerConnection(final int bufferSize, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints) {
        this(bufferSize, bufferSize, charDecoder, charEncoder, constraints, null, null, null, null);
    }
    
    public DefaultBHttpServerConnection(final int bufferSize) {
        this(bufferSize, bufferSize, null, null, null, null, null, null, null);
    }
    
    protected void onRequestReceived(final HttpRequest request) {
    }
    
    protected void onResponseSubmitted(final HttpResponse response) {
    }
    
    public void bind(final Socket socket) throws IOException {
        super.bind(socket);
    }
    
    @Override
    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        this.ensureOpen();
        final HttpRequest request = this.requestParser.parse();
        this.onRequestReceived(request);
        this.incrementRequestCount();
        return request;
    }
    
    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        final HttpEntity entity = this.prepareInput(request);
        request.setEntity(entity);
    }
    
    @Override
    public void sendResponseHeader(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        this.responseWriter.write(response);
        this.onResponseSubmitted(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.incrementResponseCount();
        }
    }
    
    @Override
    public void sendResponseEntity(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            return;
        }
        final OutputStream outStream = this.prepareOutput(response);
        entity.writeTo(outStream);
        outStream.close();
    }
    
    @Override
    public void flush() throws IOException {
        this.ensureOpen();
        this.doFlush();
    }
}
