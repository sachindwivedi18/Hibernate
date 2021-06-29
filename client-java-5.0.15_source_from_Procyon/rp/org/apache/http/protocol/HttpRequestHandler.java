// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;

public interface HttpRequestHandler
{
    void handle(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws HttpException, IOException;
}
