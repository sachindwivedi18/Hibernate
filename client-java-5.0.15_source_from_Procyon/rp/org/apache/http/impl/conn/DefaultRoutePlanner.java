// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import java.net.InetAddress;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.conn.UnsupportedSchemeException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.protocol.HttpClientContext;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.conn.SchemePortResolver;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.conn.routing.HttpRoutePlanner;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultRoutePlanner implements HttpRoutePlanner
{
    private final SchemePortResolver schemePortResolver;
    
    public DefaultRoutePlanner(final SchemePortResolver schemePortResolver) {
        this.schemePortResolver = ((schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE);
    }
    
    @Override
    public HttpRoute determineRoute(final HttpHost host, final HttpRequest request, final HttpContext context) throws HttpException {
        Args.notNull(request, "Request");
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        final InetAddress local = config.getLocalAddress();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = this.determineProxy(host, request, context);
        }
        HttpHost target = null;
        Label_0117: {
            if (host.getPort() <= 0) {
                try {
                    target = new HttpHost(host.getHostName(), this.schemePortResolver.resolve(host), host.getSchemeName());
                    break Label_0117;
                }
                catch (UnsupportedSchemeException ex) {
                    throw new HttpException(ex.getMessage());
                }
            }
            target = host;
        }
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        return (proxy == null) ? new HttpRoute(target, local, secure) : new HttpRoute(target, local, proxy, secure);
    }
    
    protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        return null;
    }
}
