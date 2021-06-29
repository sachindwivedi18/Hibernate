// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxRedirectStrategy extends DefaultRedirectStrategy
{
    public static final LaxRedirectStrategy INSTANCE;
    private static final String[] REDIRECT_METHODS;
    
    @Override
    protected boolean isRedirectable(final String method) {
        for (final String m : LaxRedirectStrategy.REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        INSTANCE = new LaxRedirectStrategy();
        REDIRECT_METHODS = new String[] { "GET", "POST", "HEAD", "DELETE" };
    }
}
