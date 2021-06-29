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
public class NTLMSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme();
    }
    
    @Override
    public AuthScheme create(final HttpContext context) {
        return new NTLMScheme();
    }
}
