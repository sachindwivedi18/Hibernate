// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.Header;
import rp.org.apache.http.RequestLine;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpEntityEnclosingRequest;

public class BasicHttpEntityEnclosingRequest extends BasicHttpRequest implements HttpEntityEnclosingRequest
{
    private HttpEntity entity;
    
    public BasicHttpEntityEnclosingRequest(final String method, final String uri) {
        super(method, uri);
    }
    
    public BasicHttpEntityEnclosingRequest(final String method, final String uri, final ProtocolVersion ver) {
        super(method, uri, ver);
    }
    
    public BasicHttpEntityEnclosingRequest(final RequestLine requestline) {
        super(requestline);
    }
    
    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    @Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public boolean expectContinue() {
        final Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }
}
