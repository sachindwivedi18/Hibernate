// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;

public interface ServiceUnavailableRetryStrategy
{
    boolean retryRequest(final HttpResponse p0, final int p1, final HttpContext p2);
    
    long getRetryInterval();
}
