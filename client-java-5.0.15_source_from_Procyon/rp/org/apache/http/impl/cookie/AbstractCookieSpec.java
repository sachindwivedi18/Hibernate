// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import java.util.Collection;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;
import rp.org.apache.http.util.Asserts;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import rp.org.apache.http.cookie.CookieAttributeHandler;
import java.util.Map;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.cookie.CookieSpec;

@Contract(threading = ThreadingBehavior.SAFE)
public abstract class AbstractCookieSpec implements CookieSpec
{
    private final Map<String, CookieAttributeHandler> attribHandlerMap;
    
    public AbstractCookieSpec() {
        this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(10);
    }
    
    protected AbstractCookieSpec(final HashMap<String, CookieAttributeHandler> map) {
        Asserts.notNull(map, "Attribute handler map");
        this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(map);
    }
    
    protected AbstractCookieSpec(final CommonCookieAttributeHandler... handlers) {
        this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(handlers.length);
        for (final CommonCookieAttributeHandler handler : handlers) {
            this.attribHandlerMap.put(handler.getAttributeName(), handler);
        }
    }
    
    @Deprecated
    public void registerAttribHandler(final String name, final CookieAttributeHandler handler) {
        Args.notNull(name, "Attribute name");
        Args.notNull(handler, "Attribute handler");
        this.attribHandlerMap.put(name, handler);
    }
    
    protected CookieAttributeHandler findAttribHandler(final String name) {
        return this.attribHandlerMap.get(name);
    }
    
    protected CookieAttributeHandler getAttribHandler(final String name) {
        final CookieAttributeHandler handler = this.findAttribHandler(name);
        Asserts.check(handler != null, "Handler not registered for " + name + " attribute");
        return handler;
    }
    
    protected Collection<CookieAttributeHandler> getAttribHandlers() {
        return this.attribHandlerMap.values();
    }
}
