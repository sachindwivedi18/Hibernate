// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import javax.net.ssl.SSLSession;
import rp.org.apache.http.conn.routing.HttpRoute;
import rp.org.apache.http.HttpInetConnection;

@Deprecated
public interface HttpRoutedConnection extends HttpInetConnection
{
    boolean isSecure();
    
    HttpRoute getRoute();
    
    SSLSession getSSLSession();
}
