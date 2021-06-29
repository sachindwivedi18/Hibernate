// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.scheme;

import rp.org.apache.http.conn.ConnectTimeoutException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.InetAddress;
import rp.org.apache.http.params.HttpParams;
import java.net.InetSocketAddress;
import java.net.Socket;

@Deprecated
class SchemeSocketFactoryAdaptor implements SchemeSocketFactory
{
    private final SocketFactory factory;
    
    SchemeSocketFactoryAdaptor(final SocketFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Socket connectSocket(final Socket sock, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        final String host = remoteAddress.getHostName();
        final int port = remoteAddress.getPort();
        InetAddress local = null;
        int localPort = 0;
        if (localAddress != null) {
            local = localAddress.getAddress();
            localPort = localAddress.getPort();
        }
        return this.factory.connectSocket(sock, host, port, local, localPort, params);
    }
    
    @Override
    public Socket createSocket(final HttpParams params) throws IOException {
        return this.factory.createSocket();
    }
    
    @Override
    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        return this.factory.isSecure(sock);
    }
    
    public SocketFactory getFactory() {
        return this.factory;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && (this == obj || ((obj instanceof SchemeSocketFactoryAdaptor) ? this.factory.equals(((SchemeSocketFactoryAdaptor)obj).factory) : this.factory.equals(obj)));
    }
    
    @Override
    public int hashCode() {
        return this.factory.hashCode();
    }
}
