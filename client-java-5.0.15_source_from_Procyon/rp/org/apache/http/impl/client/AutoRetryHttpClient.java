// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.conn.ClientConnectionManager;
import java.io.InterruptedIOException;
import rp.org.apache.http.util.EntityUtils;
import java.net.URI;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.client.ResponseHandler;
import java.io.IOException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.util.Args;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.client.ServiceUnavailableRetryStrategy;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.HttpClient;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class AutoRetryHttpClient implements HttpClient
{
    private final HttpClient backend;
    private final ServiceUnavailableRetryStrategy retryStrategy;
    private final Log log;
    
    public AutoRetryHttpClient(final HttpClient client, final ServiceUnavailableRetryStrategy retryStrategy) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(client, "HttpClient");
        Args.notNull(retryStrategy, "ServiceUnavailableRetryStrategy");
        this.backend = client;
        this.retryStrategy = retryStrategy;
    }
    
    public AutoRetryHttpClient() {
        this(new DefaultHttpClient(), new DefaultServiceUnavailableRetryStrategy());
    }
    
    public AutoRetryHttpClient(final ServiceUnavailableRetryStrategy config) {
        this(new DefaultHttpClient(), config);
    }
    
    public AutoRetryHttpClient(final HttpClient client) {
        this(client, new DefaultServiceUnavailableRetryStrategy());
    }
    
    @Override
    public HttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException {
        final HttpContext defaultContext = null;
        return this.execute(target, request, defaultContext);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.execute(target, request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException {
        final HttpResponse resp = this.execute(target, request, context);
        return (T)responseHandler.handleResponse(resp);
    }
    
    @Override
    public HttpResponse execute(final HttpUriRequest request) throws IOException {
        final HttpContext context = null;
        return this.execute(request, context);
    }
    
    @Override
    public HttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException {
        final URI uri = request.getURI();
        final HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        return this.execute(httpHost, request, context);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.execute(request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException {
        final HttpResponse resp = this.execute(request, context);
        return (T)responseHandler.handleResponse(resp);
    }
    
    @Override
    public HttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException {
        int c = 1;
        while (true) {
            final HttpResponse response = this.backend.execute(target, request, context);
            try {
                if (!this.retryStrategy.retryRequest(response, c, context)) {
                    return response;
                }
                EntityUtils.consume(response.getEntity());
                final long nextInterval = this.retryStrategy.getRetryInterval();
                try {
                    this.log.trace("Wait for " + nextInterval);
                    Thread.sleep(nextInterval);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                }
            }
            catch (RuntimeException ex) {
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException ioex) {
                    this.log.warn("I/O error consuming response content", ioex);
                }
                throw ex;
            }
            ++c;
        }
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return this.backend.getConnectionManager();
    }
    
    @Override
    public HttpParams getParams() {
        return this.backend.getParams();
    }
}
