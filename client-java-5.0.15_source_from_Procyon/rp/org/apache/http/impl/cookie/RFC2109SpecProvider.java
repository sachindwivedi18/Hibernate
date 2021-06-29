// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.cookie.CommonCookieAttributeHandler;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.conn.util.PublicSuffixMatcher;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.annotation.Obsolete;
import rp.org.apache.http.cookie.CookieSpecProvider;

@Obsolete
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RFC2109SpecProvider implements CookieSpecProvider
{
    private final PublicSuffixMatcher publicSuffixMatcher;
    private final boolean oneHeader;
    private volatile CookieSpec cookieSpec;
    
    public RFC2109SpecProvider(final PublicSuffixMatcher publicSuffixMatcher, final boolean oneHeader) {
        this.oneHeader = oneHeader;
        this.publicSuffixMatcher = publicSuffixMatcher;
    }
    
    public RFC2109SpecProvider(final PublicSuffixMatcher publicSuffixMatcher) {
        this(publicSuffixMatcher, false);
    }
    
    public RFC2109SpecProvider() {
        this(null, false);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new RFC2109Spec(this.oneHeader, new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler() });
                }
            }
        }
        return this.cookieSpec;
    }
}
