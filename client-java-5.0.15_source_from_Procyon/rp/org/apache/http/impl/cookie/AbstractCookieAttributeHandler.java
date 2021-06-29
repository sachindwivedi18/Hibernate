// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractCookieAttributeHandler implements CookieAttributeHandler
{
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
}
