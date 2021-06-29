// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.protocol.HttpContext;
import java.util.Collection;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieSpecProvider;
import rp.org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BrowserCompatSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final SecurityLevel securityLevel;
    private final CookieSpec cookieSpec;
    
    public BrowserCompatSpecFactory(final String[] datepatterns, final SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
        this.cookieSpec = new BrowserCompatSpec(datepatterns, securityLevel);
    }
    
    public BrowserCompatSpecFactory(final String[] datepatterns) {
        this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
    }
    
    public BrowserCompatSpecFactory() {
        this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
    }
    
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            final Collection<?> param = (Collection<?>)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            return new BrowserCompatSpec(patterns, this.securityLevel);
        }
        return new BrowserCompatSpec(null, this.securityLevel);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
    
    public enum SecurityLevel
    {
        SECURITYLEVEL_DEFAULT, 
        SECURITYLEVEL_IE_MEDIUM;
    }
}
