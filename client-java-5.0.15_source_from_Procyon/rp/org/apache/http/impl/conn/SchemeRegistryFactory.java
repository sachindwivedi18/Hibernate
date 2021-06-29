// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.conn.ssl.SSLSocketFactory;
import rp.org.apache.http.conn.scheme.SchemeSocketFactory;
import rp.org.apache.http.conn.scheme.Scheme;
import rp.org.apache.http.conn.scheme.PlainSocketFactory;
import rp.org.apache.http.conn.scheme.SchemeRegistry;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public final class SchemeRegistryFactory
{
    public static SchemeRegistry createDefault() {
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        return registry;
    }
    
    public static SchemeRegistry createSystemDefault() {
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, SSLSocketFactory.getSystemSocketFactory()));
        return registry;
    }
}
