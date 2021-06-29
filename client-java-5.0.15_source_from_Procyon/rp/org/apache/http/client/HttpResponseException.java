// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.util.TextUtils;

public class HttpResponseException extends ClientProtocolException
{
    private static final long serialVersionUID = -7186627969477257933L;
    private final int statusCode;
    private final String reasonPhrase;
    
    public HttpResponseException(final int statusCode, final String reasonPhrase) {
        super(String.format("status code: %d" + (TextUtils.isBlank(reasonPhrase) ? "" : ", reason phrase: %s"), statusCode, reasonPhrase));
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}
