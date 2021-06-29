// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth.params;

import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class AuthParamBean extends HttpAbstractParamBean
{
    public AuthParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setCredentialCharset(final String charset) {
        AuthParams.setCredentialCharset(this.params, charset);
    }
}
