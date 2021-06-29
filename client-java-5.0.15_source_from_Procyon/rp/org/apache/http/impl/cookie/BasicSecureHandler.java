// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.SetCookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicSecureHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        cookie.setSecure(true);
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        return !cookie.isSecure() || origin.isSecure();
    }
    
    @Override
    public String getAttributeName() {
        return "secure";
    }
}
