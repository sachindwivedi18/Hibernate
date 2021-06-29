// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.util.List;
import rp.org.apache.http.auth.MalformedChallengeException;
import rp.org.apache.http.Header;
import java.util.Map;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultProxyAuthenticationHandler extends AbstractAuthenticationHandler
{
    @Override
    public boolean isAuthenticationRequested(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        return status == 407;
    }
    
    @Override
    public Map<String, Header> getChallenges(final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        Args.notNull(response, "HTTP response");
        final Header[] headers = response.getHeaders("Proxy-Authenticate");
        return this.parseChallenges(headers);
    }
    
    @Override
    protected List<String> getAuthPreferences(final HttpResponse response, final HttpContext context) {
        final List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.proxy-scheme-pref");
        if (authpref != null) {
            return authpref;
        }
        return super.getAuthPreferences(response, context);
    }
}
