// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

import rp.org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy
{
    boolean keepAlive(final HttpResponse p0, final HttpContext p1);
}
