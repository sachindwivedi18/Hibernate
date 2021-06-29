// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.MalformedCookieException;
import java.util.regex.Matcher;
import java.util.Date;
import rp.org.apache.http.util.TextUtils;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.SetCookie;
import java.util.regex.Pattern;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxMaxAgeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    private static final Pattern MAX_AGE_PATTERN;
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (TextUtils.isBlank(value)) {
            return;
        }
        final Matcher matcher = LaxMaxAgeHandler.MAX_AGE_PATTERN.matcher(value);
        if (matcher.matches()) {
            int age;
            try {
                age = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                return;
            }
            final Date expiryDate = (age >= 0) ? new Date(System.currentTimeMillis() + age * 1000L) : new Date(Long.MIN_VALUE);
            cookie.setExpiryDate(expiryDate);
        }
    }
    
    @Override
    public String getAttributeName() {
        return "max-age";
    }
    
    static {
        MAX_AGE_PATTERN = Pattern.compile("^\\-?[0-9]+$");
    }
}
