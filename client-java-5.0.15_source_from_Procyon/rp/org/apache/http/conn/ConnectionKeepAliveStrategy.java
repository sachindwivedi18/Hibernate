// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;

public interface ConnectionKeepAliveStrategy
{
    long getKeepAliveDuration(final HttpResponse p0, final HttpContext p1);
}
