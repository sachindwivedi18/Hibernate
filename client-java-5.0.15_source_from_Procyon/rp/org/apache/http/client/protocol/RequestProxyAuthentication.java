// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.auth.AuthState;
import rp.org.apache.http.conn.HttpRoutedConnection;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestProxyAuthentication extends RequestAuthenticationBase
{
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        if (request.containsHeader("Proxy-Authorization")) {
            return;
        }
        final HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
        if (conn == null) {
            this.log.debug("HTTP connection not set in the context");
            return;
        }
        final HttpRoute route = conn.getRoute();
        if (route.isTunnelled()) {
            return;
        }
        final AuthState authState = (AuthState)context.getAttribute("http.auth.proxy-scope");
        if (authState == null) {
            this.log.debug("Proxy auth state not set in the context");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Proxy auth state: " + authState.getState());
        }
        this.process(authState, request, context);
    }
}
