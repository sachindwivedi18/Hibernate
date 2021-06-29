// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.SAFE)
public class RequestDate implements HttpRequestInterceptor
{
    private static final HttpDateGenerator DATE_GENERATOR;
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (request instanceof HttpEntityEnclosingRequest && !request.containsHeader("Date")) {
            final String httpdate = RequestDate.DATE_GENERATOR.getCurrentDate();
            request.setHeader("Date", httpdate);
        }
    }
    
    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }
}
