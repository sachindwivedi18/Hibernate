// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.conn.scheme.Scheme;
import java.net.InetAddress;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.util.Asserts;
import rp.org.apache.http.conn.params.ConnRouteParams;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.conn.scheme.SchemeRegistry;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.routing.HttpRoutePlanner;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class DefaultHttpRoutePlanner implements HttpRoutePlanner
{
    protected final SchemeRegistry schemeRegistry;
    
    public DefaultHttpRoutePlanner(final SchemeRegistry schreg) {
        Args.notNull(schreg, "Scheme registry");
        this.schemeRegistry = schreg;
    }
    
    @Override
    public HttpRoute determineRoute(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        Args.notNull(request, "HTTP request");
        HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
        if (route != null) {
            return route;
        }
        Asserts.notNull(target, "Target host");
        final InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
        final HttpHost proxy = ConnRouteParams.getDefaultProxy(request.getParams());
        Scheme schm;
        try {
            schm = this.schemeRegistry.getScheme(target.getSchemeName());
        }
        catch (IllegalStateException ex) {
            throw new HttpException(ex.getMessage());
        }
        final boolean secure = schm.isLayered();
        if (proxy == null) {
            route = new HttpRoute(target, local, secure);
        }
        else {
            route = new HttpRoute(target, local, proxy, secure);
        }
        return route;
    }
}
