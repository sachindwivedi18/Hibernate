// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class BestMatchSpec extends DefaultCookieSpec
{
    public BestMatchSpec(final String[] datepatterns, final boolean oneHeader) {
        super(datepatterns, oneHeader);
    }
    
    public BestMatchSpec() {
        this(null, false);
    }
    
    @Override
    public String toString() {
        return "best-match";
    }
}
