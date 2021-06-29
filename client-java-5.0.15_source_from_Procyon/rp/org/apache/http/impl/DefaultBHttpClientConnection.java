// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import java.io.OutputStream;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.util.Args;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.net.Socket;
import rp.org.apache.http.impl.io.DefaultHttpResponseParserFactory;
import rp.org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import rp.org.apache.http.io.HttpMessageParserFactory;
import rp.org.apache.http.io.HttpMessageWriterFactory;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.config.MessageConstraints;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageWriter;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.HttpClientConnection;

public class DefaultBHttpClientConnection extends BHttpConnectionBase implements HttpClientConnection
{
    private final HttpMessageParser<HttpResponse> responseParser;
    private final HttpMessageWriter<HttpRequest> requestWriter;
    
    public DefaultBHttpClientConnection(final int bufferSize, final int fragmentSizeHint, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy);
        this.requestWriter = ((requestWriterFactory != null) ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE).create(this.getSessionOutputBuffer());
        this.responseParser = ((responseParserFactory != null) ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE).create(this.getSessionInputBuffer(), constraints);
    }
    
    public DefaultBHttpClientConnection(final int bufferSize, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints) {
        this(bufferSize, bufferSize, charDecoder, charEncoder, constraints, null, null, null, null);
    }
    
    public DefaultBHttpClientConnection(final int bufferSize) {
        this(bufferSize, bufferSize, null, null, null, null, null, null, null);
    }
    
    protected void onResponseReceived(final HttpResponse response) {
    }
    
    protected void onRequestSubmitted(final HttpRequest request) {
    }
    
    public void bind(final Socket socket) throws IOException {
        super.bind(socket);
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        this.ensureOpen();
        try {
            return this.awaitInput(timeout);
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        this.requestWriter.write(request);
        this.onRequestSubmitted(request);
        this.incrementRequestCount();
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.ensureOpen();
        final HttpEntity entity = request.getEntity();
        if (entity == null) {
            return;
        }
        final OutputStream outStream = this.prepareOutput(request);
        entity.writeTo(outStream);
        outStream.close();
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        this.ensureOpen();
        final HttpResponse response = this.responseParser.parse();
        this.onResponseReceived(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.incrementResponseCount();
        }
        return response;
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.ensureOpen();
        final HttpEntity entity = this.prepareInput(response);
        response.setEntity(entity);
    }
    
    @Override
    public void flush() throws IOException {
        this.ensureOpen();
        this.doFlush();
    }
}
