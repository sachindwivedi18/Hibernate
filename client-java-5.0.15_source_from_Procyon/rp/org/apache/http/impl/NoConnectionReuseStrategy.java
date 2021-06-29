// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.ConnectionReuseStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public static final NoConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        return false;
    }
    
    static {
        INSTANCE = new NoConnectionReuseStrategy();
    }
}
