// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import rp.org.apache.http.config.ConnectionConfig;
import rp.org.apache.http.HttpConnection;

public interface HttpConnectionFactory<T, C extends HttpConnection>
{
    C create(final T p0, final ConnectionConfig p1);
}
