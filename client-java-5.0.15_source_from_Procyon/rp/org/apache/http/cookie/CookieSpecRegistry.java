// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.cookie;

import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.protocol.HttpContext;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import rp.org.apache.http.params.HttpParams;
import java.util.Locale;
import rp.org.apache.http.util.Args;
import java.util.concurrent.ConcurrentHashMap;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.config.Lookup;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public final class CookieSpecRegistry implements Lookup<CookieSpecProvider>
{
    private final ConcurrentHashMap<String, CookieSpecFactory> registeredSpecs;
    
    public CookieSpecRegistry() {
        this.registeredSpecs = new ConcurrentHashMap<String, CookieSpecFactory>();
    }
    
    public void register(final String name, final CookieSpecFactory factory) {
        Args.notNull(name, "Name");
        Args.notNull(factory, "Cookie spec factory");
        this.registeredSpecs.put(name.toLowerCase(Locale.ENGLISH), factory);
    }
    
    public void unregister(final String id) {
        Args.notNull(id, "Id");
        this.registeredSpecs.remove(id.toLowerCase(Locale.ENGLISH));
    }
    
    public CookieSpec getCookieSpec(final String name, final HttpParams params) throws IllegalStateException {
        Args.notNull(name, "Name");
        final CookieSpecFactory factory = this.registeredSpecs.get(name.toLowerCase(Locale.ENGLISH));
        if (factory != null) {
            return factory.newInstance(params);
        }
        throw new IllegalStateException("Unsupported cookie spec: " + name);
    }
    
    public CookieSpec getCookieSpec(final String name) throws IllegalStateException {
        return this.getCookieSpec(name, null);
    }
    
    public List<String> getSpecNames() {
        return new ArrayList<String>(this.registeredSpecs.keySet());
    }
    
    public void setItems(final Map<String, CookieSpecFactory> map) {
        if (map == null) {
            return;
        }
        this.registeredSpecs.clear();
        this.registeredSpecs.putAll(map);
    }
    
    @Override
    public CookieSpecProvider lookup(final String name) {
        return new CookieSpecProvider() {
            @Override
            public CookieSpec create(final HttpContext context) {
                final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
                return CookieSpecRegistry.this.getCookieSpec(name, request.getParams());
            }
        };
    }
}
