// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpResponseInterceptor;
import rp.org.apache.http.client.protocol.ResponseContentEncoding;
import rp.org.apache.http.HttpRequestInterceptor;
import rp.org.apache.http.client.protocol.RequestAcceptEncoding;
import rp.org.apache.http.protocol.BasicHttpProcessor;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class ContentEncodingHttpClient extends DefaultHttpClient
{
    public ContentEncodingHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
    }
    
    public ContentEncodingHttpClient(final HttpParams params) {
        this(null, params);
    }
    
    public ContentEncodingHttpClient() {
        this((HttpParams)null);
    }
    
    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor result = super.createHttpProcessor();
        result.addRequestInterceptor(new RequestAcceptEncoding());
        result.addResponseInterceptor(new ResponseContentEncoding());
        return result;
    }
}
