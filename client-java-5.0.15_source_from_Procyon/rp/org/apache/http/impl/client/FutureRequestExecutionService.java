// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.io.IOException;
import rp.org.apache.http.concurrent.FutureCallback;
import rp.org.apache.http.client.ResponseHandler;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.client.methods.HttpUriRequest;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import rp.org.apache.http.client.HttpClient;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Closeable;

@Contract(threading = ThreadingBehavior.SAFE)
public class FutureRequestExecutionService implements Closeable
{
    private final HttpClient httpclient;
    private final ExecutorService executorService;
    private final FutureRequestExecutionMetrics metrics;
    private final AtomicBoolean closed;
    
    public FutureRequestExecutionService(final HttpClient httpclient, final ExecutorService executorService) {
        this.metrics = new FutureRequestExecutionMetrics();
        this.closed = new AtomicBoolean(false);
        this.httpclient = httpclient;
        this.executorService = executorService;
    }
    
    public <T> HttpRequestFutureTask<T> execute(final HttpUriRequest request, final HttpContext context, final ResponseHandler<T> responseHandler) {
        return this.execute(request, context, responseHandler, null);
    }
    
    public <T> HttpRequestFutureTask<T> execute(final HttpUriRequest request, final HttpContext context, final ResponseHandler<T> responseHandler, final FutureCallback<T> callback) {
        if (this.closed.get()) {
            throw new IllegalStateException("Close has been called on this httpclient instance.");
        }
        this.metrics.getScheduledConnections().incrementAndGet();
        final HttpRequestTaskCallable<T> callable = new HttpRequestTaskCallable<T>(this.httpclient, request, context, responseHandler, callback, this.metrics);
        final HttpRequestFutureTask<T> httpRequestFutureTask = new HttpRequestFutureTask<T>(request, callable);
        this.executorService.execute(httpRequestFutureTask);
        return httpRequestFutureTask;
    }
    
    public FutureRequestExecutionMetrics metrics() {
        return this.metrics;
    }
    
    @Override
    public void close() throws IOException {
        this.closed.set(true);
        this.executorService.shutdownNow();
        if (this.httpclient instanceof Closeable) {
            ((Closeable)this.httpclient).close();
        }
    }
}
