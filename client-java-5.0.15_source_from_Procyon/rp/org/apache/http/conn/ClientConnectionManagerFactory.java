// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import rp.org.apache.http.conn.scheme.SchemeRegistry;
import rp.org.apache.http.params.HttpParams;

@Deprecated
public interface ClientConnectionManagerFactory
{
    ClientConnectionManager newInstance(final HttpParams p0, final SchemeRegistry p1);
}
