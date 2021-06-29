// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.auth.MalformedChallengeException;
import rp.org.apache.http.Header;
import java.util.Queue;
import rp.org.apache.http.HttpResponse;
import java.util.Map;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.auth.AuthScheme;
import rp.org.apache.http.HttpHost;
import java.util.Collection;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final ProxyAuthenticationStrategy INSTANCE;
    
    public ProxyAuthenticationStrategy() {
        super(407, "Proxy-Authenticate");
    }
    
    @Override
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getProxyPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new ProxyAuthenticationStrategy();
    }
}
