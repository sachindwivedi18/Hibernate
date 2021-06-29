// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.util.Locale;
import rp.org.apache.http.HttpRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class StandardHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler
{
    private final Map<String, Boolean> idempotentMethods;
    
    public StandardHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
        super(retryCount, requestSentRetryEnabled);
        (this.idempotentMethods = new ConcurrentHashMap<String, Boolean>()).put("GET", Boolean.TRUE);
        this.idempotentMethods.put("HEAD", Boolean.TRUE);
        this.idempotentMethods.put("PUT", Boolean.TRUE);
        this.idempotentMethods.put("DELETE", Boolean.TRUE);
        this.idempotentMethods.put("OPTIONS", Boolean.TRUE);
        this.idempotentMethods.put("TRACE", Boolean.TRUE);
    }
    
    public StandardHttpRequestRetryHandler() {
        this(3, false);
    }
    
    @Override
    protected boolean handleAsIdempotent(final HttpRequest request) {
        final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
        final Boolean b = this.idempotentMethods.get(method);
        return b != null && b;
    }
}
