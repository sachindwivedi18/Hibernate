// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.conn.routing.HttpRoute;

@Deprecated
public class RoutedRequest
{
    protected final RequestWrapper request;
    protected final HttpRoute route;
    
    public RoutedRequest(final RequestWrapper req, final HttpRoute route) {
        this.request = req;
        this.route = route;
    }
    
    public final RequestWrapper getRequest() {
        return this.request;
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}
