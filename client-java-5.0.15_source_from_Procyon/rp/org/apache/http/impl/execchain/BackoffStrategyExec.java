// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.execchain;

import rp.org.apache.http.HttpResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import rp.org.apache.http.client.methods.HttpExecutionAware;
import rp.org.apache.http.client.protocol.HttpClientContext;
import rp.org.apache.http.client.methods.HttpRequestWrapper;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.client.BackoffManager;
import rp.org.apache.http.client.ConnectionBackoffStrategy;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class BackoffStrategyExec implements ClientExecChain
{
    private final ClientExecChain requestExecutor;
    private final ConnectionBackoffStrategy connectionBackoffStrategy;
    private final BackoffManager backoffManager;
    
    public BackoffStrategyExec(final ClientExecChain requestExecutor, final ConnectionBackoffStrategy connectionBackoffStrategy, final BackoffManager backoffManager) {
        Args.notNull(requestExecutor, "HTTP client request executor");
        Args.notNull(connectionBackoffStrategy, "Connection backoff strategy");
        Args.notNull(backoffManager, "Backoff manager");
        this.requestExecutor = requestExecutor;
        this.connectionBackoffStrategy = connectionBackoffStrategy;
        this.backoffManager = backoffManager;
    }
    
    @Override
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        CloseableHttpResponse out = null;
        try {
            out = this.requestExecutor.execute(route, request, context, execAware);
        }
        catch (Exception ex) {
            if (out != null) {
                out.close();
            }
            if (this.connectionBackoffStrategy.shouldBackoff(ex)) {
                this.backoffManager.backOff(route);
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            if (ex instanceof HttpException) {
                throw (HttpException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new UndeclaredThrowableException(ex);
        }
        if (this.connectionBackoffStrategy.shouldBackoff(out)) {
            this.backoffManager.backOff(route);
        }
        else {
            this.backoffManager.probe(route);
        }
        return out;
    }
}
