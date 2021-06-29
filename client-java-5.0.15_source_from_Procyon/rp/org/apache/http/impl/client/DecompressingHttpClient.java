// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.client.ResponseHandler;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.protocol.BasicHttpContext;
import java.net.URI;
import rp.org.apache.http.client.utils.URIUtils;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.client.protocol.ResponseContentEncoding;
import rp.org.apache.http.client.protocol.RequestAcceptEncoding;
import rp.org.apache.http.HttpResponseInterceptor;
import rp.org.apache.http.HttpRequestInterceptor;
import rp.org.apache.http.client.HttpClient;

@Deprecated
public class DecompressingHttpClient implements HttpClient
{
    private final HttpClient backend;
    private final HttpRequestInterceptor acceptEncodingInterceptor;
    private final HttpResponseInterceptor contentEncodingInterceptor;
    
    public DecompressingHttpClient() {
        this(new DefaultHttpClient());
    }
    
    public DecompressingHttpClient(final HttpClient backend) {
        this(backend, new RequestAcceptEncoding(), new ResponseContentEncoding());
    }
    
    DecompressingHttpClient(final HttpClient backend, final HttpRequestInterceptor requestInterceptor, final HttpResponseInterceptor responseInterceptor) {
        this.backend = backend;
        this.acceptEncodingInterceptor = requestInterceptor;
        this.contentEncodingInterceptor = responseInterceptor;
    }
    
    @Override
    public HttpParams getParams() {
        return this.backend.getParams();
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return this.backend.getConnectionManager();
    }
    
    @Override
    public HttpResponse execute(final HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.execute(this.getHttpHost(request), request, (HttpContext)null);
    }
    
    public HttpClient getHttpClient() {
        return this.backend;
    }
    
    HttpHost getHttpHost(final HttpUriRequest request) {
        final URI uri = request.getURI();
        return URIUtils.extractHost(uri);
    }
    
    @Override
    public HttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        return this.execute(this.getHttpHost(request), request, context);
    }
    
    @Override
    public HttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException, ClientProtocolException {
        return this.execute(target, request, (HttpContext)null);
    }
    
    @Override
    public HttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        try {
            final HttpContext localContext = (context != null) ? context : new BasicHttpContext();
            HttpRequest wrapped;
            if (request instanceof HttpEntityEnclosingRequest) {
                wrapped = new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request);
            }
            else {
                wrapped = new RequestWrapper(request);
            }
            this.acceptEncodingInterceptor.process(wrapped, localContext);
            final HttpResponse response = this.backend.execute(target, wrapped, localContext);
            try {
                this.contentEncodingInterceptor.process(response, localContext);
                if (Boolean.TRUE.equals(localContext.getAttribute("http.client.response.uncompressed"))) {
                    response.removeHeaders("Content-Length");
                    response.removeHeaders("Content-Encoding");
                    response.removeHeaders("Content-MD5");
                }
                return response;
            }
            catch (HttpException ex) {
                EntityUtils.consume(response.getEntity());
                throw ex;
            }
            catch (IOException ex2) {
                EntityUtils.consume(response.getEntity());
                throw ex2;
            }
            catch (RuntimeException ex3) {
                EntityUtils.consume(response.getEntity());
                throw ex3;
            }
        }
        catch (HttpException e) {
            throw new ClientProtocolException(e);
        }
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(this.getHttpHost(request), (HttpRequest)request, responseHandler);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        return this.execute(this.getHttpHost(request), (HttpRequest)request, responseHandler, context);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(target, request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        final HttpResponse response = this.execute(target, request, context);
        try {
            return (T)responseHandler.handleResponse(response);
        }
        finally {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        }
    }
}
