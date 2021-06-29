// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.execchain;

import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.Header;
import java.io.InterruptedIOException;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import rp.org.apache.http.client.methods.HttpExecutionAware;
import rp.org.apache.http.client.protocol.HttpClientContext;
import rp.org.apache.http.client.methods.HttpRequestWrapper;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.util.Args;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.client.ServiceUnavailableRetryStrategy;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ServiceUnavailableRetryExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final ServiceUnavailableRetryStrategy retryStrategy;
    
    public ServiceUnavailableRetryExec(final ClientExecChain requestExecutor, final ServiceUnavailableRetryStrategy retryStrategy) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(retryStrategy, "Retry strategy");
        this.requestExecutor = requestExecutor;
        this.retryStrategy = retryStrategy;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        final Header[] origheaders = request.getAllHeaders();
        int c = 1;
        while (true) {
            final CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
            try {
                if (!this.retryStrategy.retryRequest(response, c, context) || !RequestEntityProxy.isRepeatable(request)) {
                    return response;
                }
                response.close();
                final long nextInterval = this.retryStrategy.getRetryInterval();
                if (nextInterval > 0L) {
                    try {
                        this.log.trace("Wait for " + nextInterval);
                        Thread.sleep(nextInterval);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                }
                request.setHeaders(origheaders);
            }
            catch (RuntimeException ex) {
                response.close();
                throw ex;
            }
            ++c;
        }
    }
}
