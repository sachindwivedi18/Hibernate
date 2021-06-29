// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.methods;

import java.net.URI;

public class HttpDelete extends HttpRequestBase
{
    public static final String METHOD_NAME = "DELETE";
    
    public HttpDelete() {
    }
    
    public HttpDelete(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpDelete(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "DELETE";
    }
}