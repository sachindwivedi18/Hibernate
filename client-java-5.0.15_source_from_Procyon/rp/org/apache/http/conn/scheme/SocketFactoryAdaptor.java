// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn.scheme;

import rp.org.apache.http.conn.ConnectTimeoutException;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.params.BasicHttpParams;
import java.net.Socket;

@Deprecated
class SocketFactoryAdaptor implements SocketFactory
{
    private final SchemeSocketFactory factory;
    
    SocketFactoryAdaptor(final SchemeSocketFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        final HttpParams params = new BasicHttpParams();
        return this.factory.createSocket(params);
    }
    
    @Override
    public Socket connectSocket(final Socket socket, final String host, final int port, final InetAddress localAddress, final int localPort, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            local = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
        }
        final InetAddress remoteAddress = InetAddress.getByName(host);
        final InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
        return this.factory.connectSocket(socket, remote, local, params);
    }
    
    @Override
    public boolean isSecure(final Socket socket) throws IllegalArgumentException {
        return this.factory.isSecure(socket);
    }
    
    public SchemeSocketFactory getFactory() {
        return this.factory;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && (this == obj || ((obj instanceof SocketFactoryAdaptor) ? this.factory.equals(((SocketFactoryAdaptor)obj).factory) : this.factory.equals(obj)));
    }
    
    @Override
    public int hashCode() {
        return this.factory.hashCode();
    }
}
