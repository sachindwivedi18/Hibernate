// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import rp.org.apache.http.params.HttpParams;
import java.io.IOException;
import java.net.Socket;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.HttpInetConnection;
import rp.org.apache.http.HttpClientConnection;

@Deprecated
public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection
{
    HttpHost getTargetHost();
    
    boolean isSecure();
    
    Socket getSocket();
    
    void opening(final Socket p0, final HttpHost p1) throws IOException;
    
    void openCompleted(final boolean p0, final HttpParams p1) throws IOException;
    
    void update(final Socket p0, final HttpHost p1, final boolean p2, final HttpParams p3) throws IOException;
}