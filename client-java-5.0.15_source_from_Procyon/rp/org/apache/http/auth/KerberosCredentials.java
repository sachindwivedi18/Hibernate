// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import java.security.Principal;
import org.ietf.jgss.GSSCredential;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Serializable;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosCredentials implements Credentials, Serializable
{
    private static final long serialVersionUID = 487421613855550713L;
    private final GSSCredential gssCredential;
    
    public KerberosCredentials(final GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }
    
    public GSSCredential getGSSCredential() {
        return this.gssCredential;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return null;
    }
    
    @Override
    public String getPassword() {
        return null;
    }
}
