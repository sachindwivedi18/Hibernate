// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;

@Deprecated
public interface RequestDirector
{
    HttpResponse execute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws HttpException, IOException;
}
