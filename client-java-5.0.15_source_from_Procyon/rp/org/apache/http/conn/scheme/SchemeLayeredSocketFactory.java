// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.scheme;

import java.net.UnknownHostException;
import java.io.IOException;
import rp.org.apache.http.params.HttpParams;
import java.net.Socket;

@Deprecated
public interface SchemeLayeredSocketFactory extends SchemeSocketFactory
{
    Socket createLayeredSocket(final Socket p0, final String p1, final int p2, final HttpParams p3) throws IOException, UnknownHostException;
}
