// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.TokenIterator;
import rp.org.apache.http.Header;
import rp.org.apache.http.HeaderIterator;
import rp.org.apache.http.message.BasicTokenIterator;
import rp.org.apache.http.message.BasicHeaderIterator;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.impl.DefaultConnectionReuseStrategy;

public class DefaultClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy
{
    public static final DefaultClientConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (request != null) {
            final Header[] connHeaders = request.getHeaders("Connection");
            if (connHeaders.length != 0) {
                final TokenIterator ti = new BasicTokenIterator(new BasicHeaderIterator(connHeaders, null));
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                }
            }
        }
        return super.keepAlive(response, context);
    }
    
    static {
        INSTANCE = new DefaultClientConnectionReuseStrategy();
    }
}
