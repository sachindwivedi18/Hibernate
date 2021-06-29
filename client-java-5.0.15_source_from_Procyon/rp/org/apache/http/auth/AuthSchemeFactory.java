// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import rp.org.apache.http.params.HttpParams;

@Deprecated
public interface AuthSchemeFactory
{
    AuthScheme newInstance(final HttpParams p0);
}
