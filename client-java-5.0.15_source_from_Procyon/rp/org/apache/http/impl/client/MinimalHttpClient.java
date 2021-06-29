// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.conn.scheme.SchemeRegistry;
import java.util.concurrent.TimeUnit;
import rp.org.apache.http.conn.ManagedClientConnection;
import rp.org.apache.http.conn.ClientConnectionRequest;
import rp.org.apache.http.conn.ClientConnectionManager;
import java.io.IOException;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.ClientProtocolException;
import rp.org.apache.http.client.methods.Configurable;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.client.protocol.HttpClientContext;
import rp.org.apache.http.protocol.BasicHttpContext;
import rp.org.apache.http.client.methods.HttpRequestWrapper;
import rp.org.apache.http.client.methods.HttpExecutionAware;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.params.BasicHttpParams;
import rp.org.apache.http.conn.ConnectionKeepAliveStrategy;
import rp.org.apache.http.ConnectionReuseStrategy;
import rp.org.apache.http.impl.DefaultConnectionReuseStrategy;
import rp.org.apache.http.protocol.HttpRequestExecutor;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.impl.execchain.MinimalClientExec;
import rp.org.apache.http.conn.HttpClientConnectionManager;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
class MinimalHttpClient extends CloseableHttpClient
{
    private final HttpClientConnectionManager connManager;
    private final MinimalClientExec requestExecutor;
    private final HttpParams params;
    
    public MinimalHttpClient(final HttpClientConnectionManager connManager) {
        this.connManager = Args.notNull(connManager, "HTTP connection manager");
        this.requestExecutor = new MinimalClientExec(new HttpRequestExecutor(), connManager, DefaultConnectionReuseStrategy.INSTANCE, DefaultConnectionKeepAliveStrategy.INSTANCE);
        this.params = new BasicHttpParams();
    }
    
    @Override
    protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(target, "Target host");
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware)request;
        }
        try {
            final HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request);
            final HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : new BasicHttpContext());
            final HttpRoute route = new HttpRoute(target);
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable)request).getConfig();
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            return this.requestExecutor.execute(route, wrapper, localcontext, execAware);
        }
        catch (HttpException httpException) {
            throw new ClientProtocolException(httpException);
        }
    }
    
    @Override
    public HttpParams getParams() {
        return this.params;
    }
    
    @Override
    public void close() {
        this.connManager.shutdown();
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return new ClientConnectionManager() {
            @Override
            public void shutdown() {
                MinimalHttpClient.this.connManager.shutdown();
            }
            
            @Override
            public ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void releaseConnection(final ManagedClientConnection conn, final long validDuration, final TimeUnit timeUnit) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public SchemeRegistry getSchemeRegistry() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void closeIdleConnections(final long idletime, final TimeUnit timeUnit) {
                MinimalHttpClient.this.connManager.closeIdleConnections(idletime, timeUnit);
            }
            
            @Override
            public void closeExpiredConnections() {
                MinimalHttpClient.this.connManager.closeExpiredConnections();
            }
        };
    }
}
