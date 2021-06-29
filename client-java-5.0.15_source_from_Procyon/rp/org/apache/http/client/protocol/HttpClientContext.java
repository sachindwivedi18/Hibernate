// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.auth.AuthState;
import rp.org.apache.http.client.AuthCache;
import rp.org.apache.http.client.CredentialsProvider;
import rp.org.apache.http.auth.AuthSchemeProvider;
import rp.org.apache.http.cookie.CookieSpecProvider;
import rp.org.apache.http.config.Lookup;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.client.CookieStore;
import java.net.URI;
import java.util.List;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.conn.routing.RouteInfo;
import rp.org.apache.http.protocol.BasicHttpContext;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.protocol.HttpCoreContext;

public class HttpClientContext extends HttpCoreContext
{
    public static final String HTTP_ROUTE = "http.route";
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    public static final String COOKIESPEC_REGISTRY = "http.cookiespec-registry";
    public static final String COOKIE_SPEC = "http.cookie-spec";
    public static final String COOKIE_ORIGIN = "http.cookie-origin";
    public static final String COOKIE_STORE = "http.cookie-store";
    public static final String CREDS_PROVIDER = "http.auth.credentials-provider";
    public static final String AUTH_CACHE = "http.auth.auth-cache";
    public static final String TARGET_AUTH_STATE = "http.auth.target-scope";
    public static final String PROXY_AUTH_STATE = "http.auth.proxy-scope";
    public static final String USER_TOKEN = "http.user-token";
    public static final String AUTHSCHEME_REGISTRY = "http.authscheme-registry";
    public static final String REQUEST_CONFIG = "http.request-config";
    
    public static HttpClientContext adapt(final HttpContext context) {
        return (HttpClientContext)((context instanceof HttpClientContext) ? context : new HttpClientContext(context));
    }
    
    public static HttpClientContext create() {
        return new HttpClientContext(new BasicHttpContext());
    }
    
    public HttpClientContext(final HttpContext context) {
        super(context);
    }
    
    public HttpClientContext() {
    }
    
    public RouteInfo getHttpRoute() {
        return this.getAttribute("http.route", HttpRoute.class);
    }
    
    public List<URI> getRedirectLocations() {
        return this.getAttribute("http.protocol.redirect-locations", (Class<List<URI>>)List.class);
    }
    
    public CookieStore getCookieStore() {
        return this.getAttribute("http.cookie-store", CookieStore.class);
    }
    
    public void setCookieStore(final CookieStore cookieStore) {
        this.setAttribute("http.cookie-store", cookieStore);
    }
    
    public CookieSpec getCookieSpec() {
        return this.getAttribute("http.cookie-spec", CookieSpec.class);
    }
    
    public CookieOrigin getCookieOrigin() {
        return this.getAttribute("http.cookie-origin", CookieOrigin.class);
    }
    
    private <T> Lookup<T> getLookup(final String name, final Class<T> clazz) {
        return this.getAttribute(name, (Class<Lookup<T>>)Lookup.class);
    }
    
    public Lookup<CookieSpecProvider> getCookieSpecRegistry() {
        return this.getLookup("http.cookiespec-registry", CookieSpecProvider.class);
    }
    
    public void setCookieSpecRegistry(final Lookup<CookieSpecProvider> lookup) {
        this.setAttribute("http.cookiespec-registry", lookup);
    }
    
    public Lookup<AuthSchemeProvider> getAuthSchemeRegistry() {
        return this.getLookup("http.authscheme-registry", AuthSchemeProvider.class);
    }
    
    public void setAuthSchemeRegistry(final Lookup<AuthSchemeProvider> lookup) {
        this.setAttribute("http.authscheme-registry", lookup);
    }
    
    public CredentialsProvider getCredentialsProvider() {
        return this.getAttribute("http.auth.credentials-provider", CredentialsProvider.class);
    }
    
    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.setAttribute("http.auth.credentials-provider", credentialsProvider);
    }
    
    public AuthCache getAuthCache() {
        return this.getAttribute("http.auth.auth-cache", AuthCache.class);
    }
    
    public void setAuthCache(final AuthCache authCache) {
        this.setAttribute("http.auth.auth-cache", authCache);
    }
    
    public AuthState getTargetAuthState() {
        return this.getAttribute("http.auth.target-scope", AuthState.class);
    }
    
    public AuthState getProxyAuthState() {
        return this.getAttribute("http.auth.proxy-scope", AuthState.class);
    }
    
    public <T> T getUserToken(final Class<T> clazz) {
        return this.getAttribute("http.user-token", clazz);
    }
    
    public Object getUserToken() {
        return this.getAttribute("http.user-token");
    }
    
    public void setUserToken(final Object obj) {
        this.setAttribute("http.user-token", obj);
    }
    
    public RequestConfig getRequestConfig() {
        final RequestConfig config = this.getAttribute("http.request-config", RequestConfig.class);
        return (config != null) ? config : RequestConfig.DEFAULT;
    }
    
    public void setRequestConfig(final RequestConfig config) {
        this.setAttribute("http.request-config", config);
    }
}
