// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.params;

import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ConnConnectionParamBean extends HttpAbstractParamBean
{
    public ConnConnectionParamBean(final HttpParams params) {
        super(params);
    }
    
    @Deprecated
    public void setMaxStatusLineGarbage(final int maxStatusLineGarbage) {
        this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
    }
}
