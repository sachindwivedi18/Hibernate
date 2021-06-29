// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import java.io.IOException;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.protocol.HttpContext;
import java.net.InetAddress;
import rp.org.apache.http.HttpHost;

@Deprecated
public interface ClientConnectionOperator
{
    OperatedClientConnection createConnection();
    
    void openConnection(final OperatedClientConnection p0, final HttpHost p1, final InetAddress p2, final HttpContext p3, final HttpParams p4) throws IOException;
    
    void updateSecureConnection(final OperatedClientConnection p0, final HttpHost p1, final HttpContext p2, final HttpParams p3) throws IOException;
}
