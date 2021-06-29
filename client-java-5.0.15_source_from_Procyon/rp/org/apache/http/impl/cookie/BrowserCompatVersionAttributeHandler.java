// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.SetCookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BrowserCompatVersionAttributeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        int version = 0;
        try {
            version = Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {}
        cookie.setVersion(version);
    }
    
    @Override
    public String getAttributeName() {
        return "version";
    }
}
