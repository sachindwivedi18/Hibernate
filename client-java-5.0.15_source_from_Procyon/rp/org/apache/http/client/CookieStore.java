// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import java.util.Date;
import java.util.List;
import rp.org.apache.http.cookie.Cookie;

public interface CookieStore
{
    void addCookie(final Cookie p0);
    
    List<Cookie> getCookies();
    
    boolean clearExpired(final Date p0);
    
    void clear();
}
