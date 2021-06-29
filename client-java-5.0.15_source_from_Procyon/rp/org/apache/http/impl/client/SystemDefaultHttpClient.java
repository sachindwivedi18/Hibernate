// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.impl.NoConnectionReuseStrategy;
import rp.org.apache.http.impl.DefaultConnectionReuseStrategy;
import rp.org.apache.http.ConnectionReuseStrategy;
import rp.org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import java.net.ProxySelector;
import rp.org.apache.http.conn.routing.HttpRoutePlanner;
import rp.org.apache.http.impl.conn.PoolingClientConnectionManager;
import rp.org.apache.http.impl.conn.SchemeRegistryFactory;
import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class SystemDefaultHttpClient extends DefaultHttpClient
{
    public SystemDefaultHttpClient(final HttpParams params) {
        super(null, params);
    }
    
    public SystemDefaultHttpClient() {
        super(null, null);
    }
    
    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        final PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            s = System.getProperty("http.maxConnections", "5");
            final int max = Integer.parseInt(s);
            connmgr.setDefaultMaxPerRoute(max);
            connmgr.setMaxTotal(2 * max);
        }
        return connmgr;
    }
    
    @Override
    protected HttpRoutePlanner createHttpRoutePlanner() {
        return new ProxySelectorRoutePlanner(this.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
    }
    
    @Override
    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        final String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            return new DefaultConnectionReuseStrategy();
        }
        return new NoConnectionReuseStrategy();
    }
}
