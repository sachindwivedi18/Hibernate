// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.cookie.SetCookie2;
import rp.org.apache.http.cookie.SetCookie;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RFC2965CommentUrlAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String commenturl) throws MalformedCookieException {
        if (cookie instanceof SetCookie2) {
            final SetCookie2 cookie2 = (SetCookie2)cookie;
            cookie2.setCommentURL(commenturl);
        }
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
    
    @Override
    public String getAttributeName() {
        return "commenturl";
    }
}
