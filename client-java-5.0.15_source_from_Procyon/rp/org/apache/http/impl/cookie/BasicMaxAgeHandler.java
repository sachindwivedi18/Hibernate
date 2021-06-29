// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import java.util.Date;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.SetCookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'max-age' attribute");
        }
        int age;
        try {
            age = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid 'max-age' attribute: " + value);
        }
        if (age < 0) {
            throw new MalformedCookieException("Negative 'max-age' attribute: " + value);
        }
        cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
    }
    
    @Override
    public String getAttributeName() {
        return "max-age";
    }
}
