// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.MalformedCookieException;
import java.util.Collections;
import rp.org.apache.http.cookie.Cookie;
import java.util.List;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.Header;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpec extends CookieSpecBase
{
    @Override
    public int getVersion() {
        return 0;
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        return Collections.emptyList();
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return false;
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        return Collections.emptyList();
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
}
