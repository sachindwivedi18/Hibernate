// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import java.net.InetAddress;
import rp.org.apache.http.HttpConnection;
import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.HttpInetConnection;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestTargetHost implements HttpRequestInterceptor
{
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        final HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT") && ver.lessEquals(HttpVersion.HTTP_1_0)) {
            return;
        }
        if (!request.containsHeader("Host")) {
            HttpHost targetHost = coreContext.getTargetHost();
            if (targetHost == null) {
                final HttpConnection conn = coreContext.getConnection();
                if (conn instanceof HttpInetConnection) {
                    final InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
                    final int port = ((HttpInetConnection)conn).getRemotePort();
                    if (address != null) {
                        targetHost = new HttpHost(address.getHostName(), port);
                    }
                }
                if (targetHost == null) {
                    if (ver.lessEquals(HttpVersion.HTTP_1_0)) {
                        return;
                    }
                    throw new ProtocolException("Target host missing");
                }
            }
            request.addHeader("Host", targetHost.toHostString());
        }
    }
}
