// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class StrictHostnameVerifier extends AbstractVerifier
{
    public static final StrictHostnameVerifier INSTANCE;
    
    @Override
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
        this.verify(host, cns, subjectAlts, true);
    }
    
    @Override
    public final String toString() {
        return "STRICT";
    }
    
    static {
        INSTANCE = new StrictHostnameVerifier();
    }
}
