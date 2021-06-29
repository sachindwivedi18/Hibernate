// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.HeaderElementIterator;
import rp.org.apache.http.message.BasicHeaderElementIterator;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.ConnectionKeepAliveStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy
{
    public static final DefaultConnectionKeepAliveStrategy INSTANCE;
    
    @Override
    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            final HeaderElement he = it.nextElement();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000L;
                }
                catch (NumberFormatException ex) {}
            }
        }
        return -1L;
    }
    
    static {
        INSTANCE = new DefaultConnectionKeepAliveStrategy();
    }
}
