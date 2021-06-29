// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.protocol.HttpContext;

public interface AuthSchemeProvider
{
    AuthScheme create(final HttpContext p0);
}
