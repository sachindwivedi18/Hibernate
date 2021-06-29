// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.cookie.CookieSpec;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieSpecProvider;
import rp.org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        return new IgnoreSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return new IgnoreSpec();
    }
}
