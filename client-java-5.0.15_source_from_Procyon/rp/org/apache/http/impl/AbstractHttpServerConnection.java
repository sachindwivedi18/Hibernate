// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.HttpConnectionMetrics;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.io.HttpTransportMetrics;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.impl.io.HttpResponseWriter;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.impl.io.DefaultHttpRequestParser;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.HttpRequestFactory;
import rp.org.apache.http.impl.entity.StrictContentLengthStrategy;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import rp.org.apache.http.impl.entity.LaxContentLengthStrategy;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageWriter;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.io.EofSensor;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.impl.entity.EntityDeserializer;
import rp.org.apache.http.impl.entity.EntitySerializer;
import rp.org.apache.http.HttpServerConnection;

@Deprecated
public abstract class AbstractHttpServerConnection implements HttpServerConnection
{
    private final EntitySerializer entityserializer;
    private final EntityDeserializer entitydeserializer;
    private SessionInputBuffer inBuffer;
    private SessionOutputBuffer outbuffer;
    private EofSensor eofSensor;
    private HttpMessageParser<HttpRequest> requestParser;
    private HttpMessageWriter<HttpResponse> responseWriter;
    private HttpConnectionMetricsImpl metrics;
    
    public AbstractHttpServerConnection() {
        this.inBuffer = null;
        this.outbuffer = null;
        this.eofSensor = null;
        this.requestParser = null;
        this.responseWriter = null;
        this.metrics = null;
        this.entityserializer = this.createEntitySerializer();
        this.entitydeserializer = this.createEntityDeserializer();
    }
    
    protected abstract void assertOpen() throws IllegalStateException;
    
    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0)));
    }
    
    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }
    
    protected HttpRequestFactory createHttpRequestFactory() {
        return DefaultHttpRequestFactory.INSTANCE;
    }
    
    protected HttpMessageParser<HttpRequest> createRequestParser(final SessionInputBuffer buffer, final HttpRequestFactory requestFactory, final HttpParams params) {
        return new DefaultHttpRequestParser(buffer, null, requestFactory, params);
    }
    
    protected HttpMessageWriter<HttpResponse> createResponseWriter(final SessionOutputBuffer buffer, final HttpParams params) {
        return new HttpResponseWriter(buffer, null, params);
    }
    
    protected HttpConnectionMetricsImpl createConnectionMetrics(final HttpTransportMetrics inTransportMetric, final HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }
    
    protected void init(final SessionInputBuffer inBuffer, final SessionOutputBuffer outbuffer, final HttpParams params) {
        this.inBuffer = Args.notNull(inBuffer, "Input session buffer");
        this.outbuffer = Args.notNull(outbuffer, "Output session buffer");
        if (inBuffer instanceof EofSensor) {
            this.eofSensor = (EofSensor)inBuffer;
        }
        this.requestParser = this.createRequestParser(inBuffer, this.createHttpRequestFactory(), params);
        this.responseWriter = this.createResponseWriter(outbuffer, params);
        this.metrics = this.createConnectionMetrics(inBuffer.getMetrics(), outbuffer.getMetrics());
    }
    
    @Override
    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        this.assertOpen();
        final HttpRequest request = this.requestParser.parse();
        this.metrics.incrementRequestCount();
        return request;
    }
    
    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        this.assertOpen();
        final HttpEntity entity = this.entitydeserializer.deserialize(this.inBuffer, request);
        request.setEntity(entity);
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
    public void sendResponseHeader(final HttpResponse response) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        this.assertOpen();
        this.responseWriter.write(response);
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
    }
    
    @Override
    public void sendResponseEntity(final HttpResponse response) throws HttpException, IOException {
        if (response.getEntity() == null) {
            return;
        }
        this.entityserializer.serialize(this.outbuffer, response, response.getEntity());
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
        catch (IOException ex) {
            return true;
        }
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}
