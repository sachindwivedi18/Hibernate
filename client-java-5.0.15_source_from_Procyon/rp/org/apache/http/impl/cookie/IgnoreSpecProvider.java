// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieSpecProvider;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpecProvider implements CookieSpecProvider
{
    private volatile CookieSpec cookieSpec;
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new IgnoreSpec();
                }
            }
        }
        return this.cookieSpec;
    }
}
