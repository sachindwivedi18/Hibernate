// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.ResponseHandler;
import java.net.URI;
import rp.org.apache.http.client.utils.URIUtils;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Closeable;
import rp.org.apache.http.client.HttpClient;

@Contract(threading = ThreadingBehavior.SAFE)
public abstract class CloseableHttpClient implements HttpClient, Closeable
{
    private final Log log;
    
    public CloseableHttpClient() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    protected abstract CloseableHttpResponse doExecute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
    @Override
    public CloseableHttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, context);
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        return this.doExecute(determineTarget(request), request, context);
    }
    
    private static HttpHost determineTarget(final HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        final URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
            }
        }
        return target;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.execute(request, (HttpContext)null);
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException, ClientProtocolException {
        return this.doExecute(target, request, null);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        final HttpHost target = determineTarget(request);
        return this.execute(target, (HttpRequest)request, responseHandler, context);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.execute(target, request, responseHandler, (HttpContext)null);
    }
    
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(responseHandler, "Response handler");
        final CloseableHttpResponse response = this.execute(target, request, context);
        try {
            final T result = (T)responseHandler.handleResponse(response);
            final HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
            return result;
        }
        catch (ClientProtocolException t3) {
            final HttpEntity entity = response.getEntity();
            try {
                EntityUtils.consume(entity);
            }
            catch (Exception t2) {
                this.log.warn("Error consuming content after an exception.", t2);
            }
            throw t3;
        }
        finally {
            response.close();
        }
    }
}
