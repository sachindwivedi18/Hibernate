// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.auth;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.auth.AuthScheme;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.auth.AuthSchemeProvider;
import rp.org.apache.http.auth.AuthSchemeFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final boolean stripPort;
    private final boolean useCanonicalHostname;
    
    public KerberosSchemeFactory(final boolean stripPort, final boolean useCanonicalHostname) {
        this.stripPort = stripPort;
        this.useCanonicalHostname = useCanonicalHostname;
    }
    
    public KerberosSchemeFactory(final boolean stripPort) {
        this.stripPort = stripPort;
        this.useCanonicalHostname = true;
    }
    
    public KerberosSchemeFactory() {
        this(true, true);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
    
    public boolean isUseCanonicalHostname() {
        return this.useCanonicalHostname;
    }
    
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new KerberosScheme(this.stripPort, this.useCanonicalHostname);
    }
    
    @Override
    public AuthScheme create(final HttpContext context) {
        return new KerberosScheme(this.stripPort, this.useCanonicalHostname);
    }
}
