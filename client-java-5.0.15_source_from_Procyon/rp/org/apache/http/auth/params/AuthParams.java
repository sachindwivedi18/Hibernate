// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth.params;

import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class AuthParams
{
    private AuthParams() {
    }
    
    public static String getCredentialCharset(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        String charset = (String)params.getParameter("http.auth.credential-charset");
        if (charset == null) {
            charset = HTTP.DEF_PROTOCOL_CHARSET.name();
        }
        return charset;
    }
    
    public static void setCredentialCharset(final HttpParams params, final String charset) {
        Args.notNull(params, "HTTP parameters");
        params.setParameter("http.auth.credential-charset", charset);
    }
}
