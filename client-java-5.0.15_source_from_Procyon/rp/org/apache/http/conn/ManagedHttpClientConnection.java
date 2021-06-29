// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Socket;
import rp.org.apache.http.HttpInetConnection;
import rp.org.apache.http.HttpClientConnection;

public interface ManagedHttpClientConnection extends HttpClientConnection, HttpInetConnection
{
    String getId();
    
    void bind(final Socket p0) throws IOException;
    
    Socket getSocket();
    
    SSLSession getSSLSession();
}
