// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.conn.UnsupportedSchemeException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.SchemePortResolver;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultSchemePortResolver implements SchemePortResolver
{
    public static final DefaultSchemePortResolver INSTANCE;
    
    @Override
    public int resolve(final HttpHost host) throws UnsupportedSchemeException {
        Args.notNull(host, "HTTP host");
        final int port = host.getPort();
        if (port > 0) {
            return port;
        }
        final String name = host.getSchemeName();
        if (name.equalsIgnoreCase("http")) {
            return 80;
        }
        if (name.equalsIgnoreCase("https")) {
            return 443;
        }
        throw new UnsupportedSchemeException(name + " protocol is not supported");
    }
    
    static {
        INSTANCE = new DefaultSchemePortResolver();
    }
}
