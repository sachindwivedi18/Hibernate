// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import rp.org.apache.http.HttpRequest;

public interface HttpRequestHandlerMapper
{
    HttpRequestHandler lookup(final HttpRequest p0);
}
