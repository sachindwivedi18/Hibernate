// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import rp.org.apache.http.client.CredentialsProvider;
import rp.org.apache.http.client.CookieStore;
import rp.org.apache.http.auth.AuthSchemeRegistry;
import rp.org.apache.http.cookie.CookieSpecRegistry;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;

@Deprecated
public class ClientContextConfigurer implements ClientContext
{
    private final HttpContext context;
    
    public ClientContextConfigurer(final HttpContext context) {
        Args.notNull(context, "HTTP context");
        this.context = context;
    }
    
    public void setCookieSpecRegistry(final CookieSpecRegistry registry) {
        this.context.setAttribute("http.cookiespec-registry", registry);
    }
    
    public void setAuthSchemeRegistry(final AuthSchemeRegistry registry) {
        this.context.setAttribute("http.authscheme-registry", registry);
    }
    
    public void setCookieStore(final CookieStore store) {
        this.context.setAttribute("http.cookie-store", store);
    }
    
    public void setCredentialsProvider(final CredentialsProvider provider) {
        this.context.setAttribute("http.auth.credentials-provider", provider);
    }
}
