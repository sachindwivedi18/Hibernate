// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.ssl;

import java.util.Arrays;
import rp.org.apache.http.util.Args;
import java.security.cert.X509Certificate;

@Deprecated
public final class PrivateKeyDetails
{
    private final String type;
    private final X509Certificate[] certChain;
    
    public PrivateKeyDetails(final String type, final X509Certificate[] certChain) {
        this.type = Args.notNull(type, "Private key type");
        this.certChain = certChain;
    }
    
    public String getType() {
        return this.type;
    }
    
    public X509Certificate[] getCertChain() {
        return this.certChain;
    }
    
    @Override
    public String toString() {
        return this.type + ':' + Arrays.toString(this.certChain);
    }
}
