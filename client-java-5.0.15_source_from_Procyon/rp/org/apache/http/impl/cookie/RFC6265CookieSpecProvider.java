// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.conn.util.PublicSuffixMatcher;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieSpecProvider;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RFC6265CookieSpecProvider implements CookieSpecProvider
{
    private final CompatibilityLevel compatibilityLevel;
    private final PublicSuffixMatcher publicSuffixMatcher;
    private volatile CookieSpec cookieSpec;
    
    public RFC6265CookieSpecProvider(final CompatibilityLevel compatibilityLevel, final PublicSuffixMatcher publicSuffixMatcher) {
        this.compatibilityLevel = ((compatibilityLevel != null) ? compatibilityLevel : CompatibilityLevel.RELAXED);
        this.publicSuffixMatcher = publicSuffixMatcher;
    }
    
    public RFC6265CookieSpecProvider(final PublicSuffixMatcher publicSuffixMatcher) {
        this(CompatibilityLevel.RELAXED, publicSuffixMatcher);
    }
    
    public RFC6265CookieSpecProvider() {
        this(CompatibilityLevel.RELAXED, null);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    switch (this.compatibilityLevel) {
                        case STRICT: {
                            this.cookieSpec = new RFC6265StrictSpec(new CommonCookieAttributeHandler[] { new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicExpiresHandler(RFC6265StrictSpec.DATE_PATTERNS) });
                            break;
                        }
                        case IE_MEDIUM_SECURITY: {
                            this.cookieSpec = new RFC6265LaxSpec(new CommonCookieAttributeHandler[] { new BasicPathHandler() {
                                    @Override
                                    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                                    }
                                }, PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicExpiresHandler(RFC6265StrictSpec.DATE_PATTERNS) });
                            break;
                        }
                        default: {
                            this.cookieSpec = new RFC6265LaxSpec(new CommonCookieAttributeHandler[] { new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher), new LaxMaxAgeHandler(), new BasicSecureHandler(), new LaxExpiresHandler() });
                            break;
                        }
                    }
                }
            }
        }
        return this.cookieSpec;
    }
    
    public enum CompatibilityLevel
    {
        STRICT, 
        RELAXED, 
        IE_MEDIUM_SECURITY;
    }
}
