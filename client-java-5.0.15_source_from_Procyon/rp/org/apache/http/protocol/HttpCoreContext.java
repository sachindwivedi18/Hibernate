// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import rp.org.apache.http.HttpHost;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpConnection;
import rp.org.apache.http.util.Args;

public class HttpCoreContext implements HttpContext
{
    public static final String HTTP_CONNECTION = "http.connection";
    public static final String HTTP_REQUEST = "http.request";
    public static final String HTTP_RESPONSE = "http.response";
    public static final String HTTP_TARGET_HOST = "http.target_host";
    public static final String HTTP_REQ_SENT = "http.request_sent";
    private final HttpContext context;
    
    public static HttpCoreContext create() {
        return new HttpCoreContext(new BasicHttpContext());
    }
    
    public static HttpCoreContext adapt(final HttpContext context) {
        Args.notNull(context, "HTTP context");
        return (HttpCoreContext)((context instanceof HttpCoreContext) ? context : new HttpCoreContext(context));
    }
    
    public HttpCoreContext(final HttpContext context) {
        this.context = context;
    }
    
    public HttpCoreContext() {
        this.context = new BasicHttpContext();
    }
    
    @Override
    public Object getAttribute(final String id) {
        return this.context.getAttribute(id);
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        this.context.setAttribute(id, obj);
    }
    
    @Override
    public Object removeAttribute(final String id) {
        return this.context.removeAttribute(id);
    }
    
    public <T> T getAttribute(final String attribname, final Class<T> clazz) {
        Args.notNull(clazz, "Attribute class");
        final Object obj = this.getAttribute(attribname);
        if (obj == null) {
            return null;
        }
        return clazz.cast(obj);
    }
    
    public <T extends HttpConnection> T getConnection(final Class<T> clazz) {
        return this.getAttribute("http.connection", clazz);
    }
    
    public HttpConnection getConnection() {
        return this.getAttribute("http.connection", HttpConnection.class);
    }
    
    public HttpRequest getRequest() {
        return this.getAttribute("http.request", HttpRequest.class);
    }
    
    public boolean isRequestSent() {
        final Boolean b = this.getAttribute("http.request_sent", Boolean.class);
        return b != null && b;
    }
    
    public HttpResponse getResponse() {
        return this.getAttribute("http.response", HttpResponse.class);
    }
    
    public void setTargetHost(final HttpHost host) {
        this.setAttribute("http.target_host", host);
    }
    
    public HttpHost getTargetHost() {
        return this.getAttribute("http.target_host", HttpHost.class);
    }
}
