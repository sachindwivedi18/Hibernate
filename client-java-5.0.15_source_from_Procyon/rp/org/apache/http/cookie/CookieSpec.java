// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.cookie;

import java.util.List;
import rp.org.apache.http.Header;
import rp.org.apache.http.annotation.Obsolete;

public interface CookieSpec
{
    @Obsolete
    int getVersion();
    
    List<Cookie> parse(final Header p0, final CookieOrigin p1) throws MalformedCookieException;
    
    void validate(final Cookie p0, final CookieOrigin p1) throws MalformedCookieException;
    
    boolean match(final Cookie p0, final CookieOrigin p1);
    
    List<Header> formatCookies(final List<Cookie> p0);
    
    @Obsolete
    Header getVersionHeader();
}
