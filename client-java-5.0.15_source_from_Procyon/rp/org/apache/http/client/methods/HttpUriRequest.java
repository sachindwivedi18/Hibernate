// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.methods;

import java.net.URI;
import rp.org.apache.http.HttpRequest;

public interface HttpUriRequest extends HttpRequest
{
    String getMethod();
    
    URI getURI();
    
    void abort() throws UnsupportedOperationException;
    
    boolean isAborted();
}
