// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.auth;

import rp.org.apache.http.util.Args;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import rp.org.apache.http.auth.AuthenticationException;
import rp.org.apache.http.Header;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.auth.Credentials;

public class SPNegoScheme extends GGSSchemeBase
{
    private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
    
    public SPNegoScheme(final boolean stripPort, final boolean useCanonicalHostname) {
        super(stripPort, useCanonicalHostname);
    }
    
    public SPNegoScheme(final boolean stripPort) {
        super(stripPort);
    }
    
    public SPNegoScheme() {
    }
    
    @Override
    public String getSchemeName() {
        return "Negotiate";
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return super.authenticate(credentials, request, context);
    }
    
    @Override
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return super.generateToken(input, authServer);
    }
    
    @Override
    protected byte[] generateToken(final byte[] input, final String authServer, final Credentials credentials) throws GSSException {
        return this.generateGSSToken(input, new Oid("1.3.6.1.5.5.2"), authServer, credentials);
    }
    
    @Override
    public String getParameter(final String name) {
        Args.notNull(name, "Parameter name");
        return null;
    }
    
    @Override
    public String getRealm() {
        return null;
    }
    
    @Override
    public boolean isConnectionBased() {
        return true;
    }
}
