// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.UserTokenHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoopUserTokenHandler implements UserTokenHandler
{
    public static final NoopUserTokenHandler INSTANCE;
    
    @Override
    public Object getUserToken(final HttpContext context) {
        return null;
    }
    
    static {
        INSTANCE = new NoopUserTokenHandler();
    }
}
