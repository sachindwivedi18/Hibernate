// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustAllStrategy implements TrustStrategy
{
    public static final TrustAllStrategy INSTANCE;
    
    @Override
    public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        return true;
    }
    
    static {
        INSTANCE = new TrustAllStrategy();
    }
}
