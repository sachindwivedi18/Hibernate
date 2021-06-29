// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import java.util.Iterator;
import java.util.List;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.conn.routing.RouteInfo;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.config.Lookup;
import rp.org.apache.http.client.CookieStore;
import rp.org.apache.http.Header;
import java.util.Date;
import rp.org.apache.http.cookie.Cookie;
import java.util.ArrayList;
import rp.org.apache.http.cookie.CookieSpecProvider;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.util.TextUtils;
import java.net.URISyntaxException;
import java.net.URI;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestAddCookies implements HttpRequestInterceptor
{
    private final Log log;
    
    public RequestAddCookies() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            return;
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        final Lookup<CookieSpecProvider> registry = clientContext.getCookieSpecRegistry();
        if (registry == null) {
            this.log.debug("CookieSpec registry not specified in HTTP context");
            return;
        }
        final HttpHost targetHost = clientContext.getTargetHost();
        if (targetHost == null) {
            this.log.debug("Target host not set in the context");
            return;
        }
        final RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug("Connection route not set in the context");
            return;
        }
        final RequestConfig config = clientContext.getRequestConfig();
        String policy = config.getCookieSpec();
        if (policy == null) {
            policy = "default";
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("CookieSpec selected: " + policy);
        }
        URI requestURI = null;
        if (request instanceof HttpUriRequest) {
            requestURI = ((HttpUriRequest)request).getURI();
        }
        else {
            try {
                requestURI = new URI(request.getRequestLine().getUri());
            }
            catch (URISyntaxException ex) {}
        }
        final String path = (requestURI != null) ? requestURI.getPath() : null;
        final String hostName = targetHost.getHostName();
        int port = targetHost.getPort();
        if (port < 0) {
            port = route.getTargetHost().getPort();
        }
        final CookieOrigin cookieOrigin = new CookieOrigin(hostName, (port >= 0) ? port : 0, TextUtils.isEmpty(path) ? "/" : path, route.isSecure());
        final CookieSpecProvider provider = registry.lookup(policy);
        if (provider == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Unsupported cookie policy: " + policy);
            }
            return;
        }
        final CookieSpec cookieSpec = provider.create(clientContext);
        final List<Cookie> cookies = cookieStore.getCookies();
        final List<Cookie> matchedCookies = new ArrayList<Cookie>();
        final Date now = new Date();
        boolean expired = false;
        for (final Cookie cookie : cookies) {
            if (!cookie.isExpired(now)) {
                if (!cookieSpec.match(cookie, cookieOrigin)) {
                    continue;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Cookie " + cookie + " match " + cookieOrigin);
                }
                matchedCookies.add(cookie);
            }
            else {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Cookie " + cookie + " expired");
                }
                expired = true;
            }
        }
        if (expired) {
            cookieStore.clearExpired(now);
        }
        if (!matchedCookies.isEmpty()) {
            final List<Header> headers = cookieSpec.formatCookies(matchedCookies);
            for (final Header header : headers) {
                request.addHeader(header);
            }
        }
        final int ver = cookieSpec.getVersion();
        if (ver > 0) {
            final Header header2 = cookieSpec.getVersionHeader();
            if (header2 != null) {
                request.addHeader(header2);
            }
        }
        context.setAttribute("http.cookie-spec", cookieSpec);
        context.setAttribute("http.cookie-origin", cookieOrigin);
    }
}
