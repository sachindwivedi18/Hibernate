// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.annotation.Obsolete;
import rp.org.apache.http.cookie.CookieSpecProvider;

@Obsolete
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDraftSpecProvider implements CookieSpecProvider
{
    private final String[] datepatterns;
    private volatile CookieSpec cookieSpec;
    
    public NetscapeDraftSpecProvider(final String[] datepatterns) {
        this.datepatterns = datepatterns;
    }
    
    public NetscapeDraftSpecProvider() {
        this(null);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new NetscapeDraftSpec(this.datepatterns);
                }
            }
        }
        return this.cookieSpec;
    }
}
