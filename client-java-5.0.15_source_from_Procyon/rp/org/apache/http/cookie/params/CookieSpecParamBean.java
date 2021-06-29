// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.cookie.params;

import java.util.Collection;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class CookieSpecParamBean extends HttpAbstractParamBean
{
    public CookieSpecParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDatePatterns(final Collection<String> patterns) {
        this.params.setParameter("http.protocol.cookie-datepatterns", patterns);
    }
    
    public void setSingleHeader(final boolean singleHeader) {
        this.params.setBooleanParameter("http.protocol.single-cookie-header", singleHeader);
    }
}
