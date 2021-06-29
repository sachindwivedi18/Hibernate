// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.protocol.HttpContext;
import java.io.IOException;

public interface HttpRequestRetryHandler
{
    boolean retryRequest(final IOException p0, final int p1, final HttpContext p2);
}
