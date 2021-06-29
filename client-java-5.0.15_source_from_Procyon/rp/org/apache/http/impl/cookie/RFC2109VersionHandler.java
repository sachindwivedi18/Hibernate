// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.CookieRestrictionViolationException;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.SetCookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RFC2109VersionHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        if (value.trim().isEmpty()) {
            throw new MalformedCookieException("Blank value for version attribute");
        }
        try {
            cookie.setVersion(Integer.parseInt(value));
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid version: " + e.getMessage());
        }
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (cookie.getVersion() < 0) {
            throw new CookieRestrictionViolationException("Cookie version may not be negative");
        }
    }
    
    @Override
    public String getAttributeName() {
        return "version";
    }
}
