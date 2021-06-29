// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseServer implements HttpResponseInterceptor
{
    private final String originServer;
    
    public ResponseServer(final String originServer) {
        this.originServer = originServer;
    }
    
    public ResponseServer() {
        this(null);
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        if (!response.containsHeader("Server") && this.originServer != null) {
            response.addHeader("Server", this.originServer);
        }
    }
}
