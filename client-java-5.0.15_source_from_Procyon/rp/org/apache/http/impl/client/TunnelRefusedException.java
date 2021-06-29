// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpException;

@Deprecated
public class TunnelRefusedException extends HttpException
{
    private static final long serialVersionUID = -8646722842745617323L;
    private final HttpResponse response;
    
    public TunnelRefusedException(final String message, final HttpResponse response) {
        super(message);
        this.response = response;
    }
    
    public HttpResponse getResponse() {
        return this.response;
    }
}
