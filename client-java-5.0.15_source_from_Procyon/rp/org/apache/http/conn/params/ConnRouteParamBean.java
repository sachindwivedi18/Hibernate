// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.params;

import rp.org.apache.http.conn.routing.HttpRoute;
import java.net.InetAddress;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ConnRouteParamBean extends HttpAbstractParamBean
{
    public ConnRouteParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDefaultProxy(final HttpHost defaultProxy) {
        this.params.setParameter("http.route.default-proxy", defaultProxy);
    }
    
    public void setLocalAddress(final InetAddress address) {
        this.params.setParameter("http.route.local-address", address);
    }
    
    public void setForcedRoute(final HttpRoute route) {
        this.params.setParameter("http.route.forced-route", route);
    }
}
