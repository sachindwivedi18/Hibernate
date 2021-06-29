// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

import java.io.IOException;
import rp.org.apache.http.protocol.HttpContext;

public interface HttpResponseInterceptor
{
    void process(final HttpResponse p0, final HttpContext p1) throws HttpException, IOException;
}
