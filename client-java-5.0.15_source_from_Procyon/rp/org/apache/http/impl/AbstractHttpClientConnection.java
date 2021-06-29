// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.HttpConnectionMetrics;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.HttpException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.io.HttpTransportMetrics;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.impl.io.HttpRequestWriter;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.impl.io.DefaultHttpResponseParser;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.HttpResponseFactory;
import rp.org.apache.http.impl.entity.StrictContentLengthStrategy;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.impl.entity.LaxContentLengthStrategy;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageWriter;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.io.EofSensor;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.impl.entity.EntityDeserializer;
import rp.org.apache.http.impl.entity.EntitySerializer;
import rp.org.apache.http.HttpClientConnection;

@Deprecated
public abstract class AbstractHttpClientConnection implements HttpClientConnection
{
    private final EntitySerializer entityserializer;
    private final EntityDeserializer entitydeserializer;
    private SessionInputBuffer inBuffer;
    private SessionOutputBuffer outbuffer;
    private EofSensor eofSensor;
    private HttpMessageParser<HttpResponse> responseParser;
    private HttpMessageWriter<HttpRequest> requestWriter;
    private HttpConnectionMetricsImpl metrics;
    
    public AbstractHttpClientConnection() {
        this.inBuffer = null;
        this.outbuffer = null;
        this.eofSensor = null;
        this.responseParser = null;
        this.requestWriter = null;
        this.metrics = null;
        this.entityserializer = this.createEntitySerializer();
        this.entitydeserializer = this.createEntityDeserializer();
    }
    
    protected abstract void assertOpen() throws IllegalStateException;
    
    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }
    
    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }
    
    protected HttpResponseFactory createHttpResponseFactory() {
        return DefaultHttpResponseFactory.INSTANCE;
    }
    
    protected HttpMessageParser<HttpResponse> createResponseParser(final SessionInputBuffer buffer, final HttpResponseFactory responseFactory, final HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }
    
    protected HttpMessageWriter<HttpRequest> createRequestWriter(final SessionOutputBuffer buffer, final HttpParams params) {
        return new HttpRequestWriter(buffer, null, params);
    }
    
    protected HttpConnectionMetricsImpl createConnectionMetrics(final HttpTransportMetrics inTransportMetric, final HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }
    
    protected void init(final SessionInputBuffer sessionInputBuffer, final SessionOutputBuffer sessionOutputBuffer, final HttpParams params) {
        this.inBuffer = Args.notNull(sessionInputBuffer, "Input session buffer");
        this.outbuffer = Args.notNull(sessionOutputBuffer, "Output session buffer");
        if (sessionInputBuffer instanceof EofSensor) {
            this.eofSensor = (EofSensor)sessionInputBuffer;
        }
        this.responseParser = this.createResponseParser(sessionInputBuffer, this.createHttpResponseFactory(), params);
        this.requestWriter = this.createRequestWriter(sessionOutputBuffer, params);
        this.metrics = this.createConnectionMetrics(sessionInputBuffer.getMetrics(), sessionOutputBuffer.getMetrics());
    }
    
    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        this.assertOpen();
        try {
            return this.inBuffer.isDataAvailable(timeout);
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.assertOpen();
        this.requestWriter.write(request);
        this.metrics.incrementRequestCount();
    }
    
    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.assertOpen();
        if (request.getEntity() == null) {
            return;
        }
        this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
    }
    
    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }
    
    @Override
    public void flush() throws IOException {
        this.assertOpen();
        this.doFlush();
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        this.assertOpen();
        final HttpResponse response = this.responseParser.parse();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
        return response;
    }
    
    @Override
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.assertOpen();
        final HttpEntity entity = this.entitydeserializer.deserialize(this.inBuffer, response);
        response.setEntity(entity);
    }
    
    protected boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }
    
    @Override
    public boolean isStale() {
        if (!this.isOpen()) {
            return true;
        }
        if (this.isEof()) {
            return true;
        }
        try {
            this.inBuffer.isDataAvailable(1);
            return this.isEof();
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
        catch (IOException ex2) {
            return true;
        }
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
