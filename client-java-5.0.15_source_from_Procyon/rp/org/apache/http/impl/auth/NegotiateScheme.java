// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.auth;

import rp.org.apache.http.util.Args;
import java.io.IOException;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import rp.org.apache.http.auth.AuthenticationException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.Header;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.auth.Credentials;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.commons.logging.Log;

@Deprecated
public class NegotiateScheme extends GGSSchemeBase
{
    private final Log log;
    private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
    private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
    private final SpnegoTokenGenerator spengoGenerator;
    
    public NegotiateScheme(final SpnegoTokenGenerator spengoGenerator, final boolean stripPort) {
        super(stripPort);
        this.log = LogFactory.getLog(this.getClass());
        this.spengoGenerator = spengoGenerator;
    }
    
    public NegotiateScheme(final SpnegoTokenGenerator spengoGenerator) {
        this(spengoGenerator, false);
    }
    
    public NegotiateScheme() {
        this(null, false);
    }
    
    @Override
    public String getSchemeName() {
        return "Negotiate";
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request) throws AuthenticationException {
        return this.authenticate(credentials, request, null);
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
        Oid negotiationOid = new Oid("1.3.6.1.5.5.2");
        byte[] token = input;
        boolean tryKerberos = false;
        try {
            token = this.generateGSSToken(token, negotiationOid, authServer, credentials);
        }
        catch (GSSException ex) {
            if (ex.getMajor() != 2) {
                throw ex;
            }
            this.log.debug("GSSException BAD_MECH, retry with Kerberos MECH");
            tryKerberos = true;
        }
        if (tryKerberos) {
            this.log.debug("Using Kerberos MECH 1.2.840.113554.1.2.2");
            negotiationOid = new Oid("1.2.840.113554.1.2.2");
            token = this.generateGSSToken(token, negotiationOid, authServer, credentials);
            if (token != null && this.spengoGenerator != null) {
                try {
                    token = this.spengoGenerator.generateSpnegoDERObject(token);
                }
                catch (IOException ex2) {
                    this.log.error(ex2.getMessage(), ex2);
                }
            }
        }
        return token;
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
