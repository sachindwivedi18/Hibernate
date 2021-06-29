// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.routing;

import rp.org.apache.http.HttpException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;

public interface HttpRoutePlanner
{
    HttpRoute determineRoute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws HttpException;
}
