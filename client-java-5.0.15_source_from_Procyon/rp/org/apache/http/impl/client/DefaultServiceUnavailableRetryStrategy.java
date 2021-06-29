// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.ServiceUnavailableRetryStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy
{
    private final int maxRetries;
    private final long retryInterval;
    
    public DefaultServiceUnavailableRetryStrategy(final int maxRetries, final int retryInterval) {
        Args.positive(maxRetries, "Max retries");
        Args.positive(retryInterval, "Retry interval");
        this.maxRetries = maxRetries;
        this.retryInterval = retryInterval;
    }
    
    public DefaultServiceUnavailableRetryStrategy() {
        this(1, 1000);
    }
    
    @Override
    public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context) {
        return executionCount <= this.maxRetries && response.getStatusLine().getStatusCode() == 503;
    }
    
    @Override
    public long getRetryInterval() {
        return this.retryInterval;
    }
}
