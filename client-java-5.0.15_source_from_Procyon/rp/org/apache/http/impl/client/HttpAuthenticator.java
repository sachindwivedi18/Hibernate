// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.auth.AuthState;
import rp.org.apache.http.client.AuthenticationStrategy;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpHost;
import rp.org.apache.commons.logging.Log;

@Deprecated
public class HttpAuthenticator extends rp.org.apache.http.impl.auth.HttpAuthenticator
{
    public HttpAuthenticator(final Log log) {
        super(log);
    }
    
    public HttpAuthenticator() {
    }
    
    public boolean authenticate(final HttpHost host, final HttpResponse response, final AuthenticationStrategy authStrategy, final AuthState authState, final HttpContext context) {
        return this.handleAuthChallenge(host, response, authStrategy, authState, context);
    }
}
