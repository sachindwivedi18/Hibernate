// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.HttpException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.conn.SchemePortResolver;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner
{
    private final HttpHost proxy;
    
    public DefaultProxyRoutePlanner(final HttpHost proxy, final SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = Args.notNull(proxy, "Proxy host");
    }
    
    public DefaultProxyRoutePlanner(final HttpHost proxy) {
        this(proxy, null);
    }
    
    @Override
    protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        return this.proxy;
    }
}
