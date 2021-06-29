// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.methods;

import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.Header;
import rp.org.apache.http.HeaderIterator;
import java.util.HashSet;
import rp.org.apache.http.util.Args;
import java.util.Set;
import rp.org.apache.http.HttpResponse;
import java.net.URI;

public class HttpOptions extends HttpRequestBase
{
    public static final String METHOD_NAME = "OPTIONS";
    
    public HttpOptions() {
    }
    
    public HttpOptions(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpOptions(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "OPTIONS";
    }
    
    public Set<String> getAllowedMethods(final HttpResponse response) {
        Args.notNull(response, "HTTP response");
        final HeaderIterator it = response.headerIterator("Allow");
        final Set<String> methods = new HashSet<String>();
        while (it.hasNext()) {
            final Header header = it.nextHeader();
            final HeaderElement[] arr$;
            final HeaderElement[] elements = arr$ = header.getElements();
            for (final HeaderElement element : arr$) {
                methods.add(element.getName());
            }
        }
        return methods;
    }
}
