// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.util.Iterator;
import java.util.List;
import rp.org.apache.http.Header;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.cookie.Cookie;
import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HeaderIterator;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.client.CookieStore;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseProcessCookies implements HttpResponseInterceptor
{
    private final Log log;
    
    public ResponseProcessCookies() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP request");
        Args.notNull(context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            this.log.debug("Cookie spec not specified in HTTP context");
            return;
        }
        final CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        final CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            this.log.debug("Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it = response.headerIterator("Set-Cookie");
        this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it = response.headerIterator("Set-Cookie2");
            this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        }
    }
    
    private void processCookies(final HeaderIterator iterator, final CookieSpec cookieSpec, final CookieOrigin cookieOrigin, final CookieStore cookieStore) {
        while (iterator.hasNext()) {
            final Header header = iterator.nextHeader();
            try {
                final List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (final Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) {
                            continue;
                        }
                        this.log.debug("Cookie accepted [" + formatCooke(cookie) + "]");
                    }
                    catch (MalformedCookieException ex) {
                        if (!this.log.isWarnEnabled()) {
                            continue;
                        }
                        this.log.warn("Cookie rejected [" + formatCooke(cookie) + "] " + ex.getMessage());
                    }
                }
            }
            catch (MalformedCookieException ex2) {
                if (!this.log.isWarnEnabled()) {
                    continue;
                }
                this.log.warn("Invalid cookie header: \"" + header + "\". " + ex2.getMessage());
            }
        }
    }
    
    private static String formatCooke(final Cookie cookie) {
        final StringBuilder buf = new StringBuilder();
        buf.append(cookie.getName());
        buf.append("=\"");
        String v = cookie.getValue();
        if (v != null) {
            if (v.length() > 100) {
                v = v.substring(0, 100) + "...";
            }
            buf.append(v);
        }
        buf.append("\"");
        buf.append(", version:");
        buf.append(Integer.toString(cookie.getVersion()));
        buf.append(", domain:");
        buf.append(cookie.getDomain());
        buf.append(", path:");
        buf.append(cookie.getPath());
        buf.append(", expiry:");
        buf.append(cookie.getExpiryDate());
        return buf.toString();
    }
}
