// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.InetAddress;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.impl.io.SocketOutputBuffer;
import rp.org.apache.http.io.SessionOutputBuffer;
import java.io.IOException;
import rp.org.apache.http.impl.io.SocketInputBuffer;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.util.Asserts;
import java.net.Socket;
import rp.org.apache.http.HttpInetConnection;

@Deprecated
public class SocketHttpClientConnection extends AbstractHttpClientConnection implements HttpInetConnection
{
    private volatile boolean open;
    private volatile Socket socket;
    
    public SocketHttpClientConnection() {
        this.socket = null;
    }
    
    protected void assertNotOpen() {
        Asserts.check(!this.open, "Connection is already open");
    }
    
    @Override
    protected void assertOpen() {
        Asserts.check(this.open, "Connection is not open");
    }
    
    protected SessionInputBuffer createSessionInputBuffer(final Socket socket, final int bufferSize, final HttpParams params) throws IOException {
        return new SocketInputBuffer(socket, bufferSize, params);
    }
    
    protected SessionOutputBuffer createSessionOutputBuffer(final Socket socket, final int bufferSize, final HttpParams params) throws IOException {
        return new SocketOutputBuffer(socket, bufferSize, params);
    }
    
    protected void bind(final Socket socket, final HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        Args.notNull(params, "HTTP parameters");
        this.socket = socket;
        final int bufferSize = params.getIntParameter("http.socket.buffer-size", -1);
        this.init(this.createSessionInputBuffer(socket, bufferSize, params), this.createSessionOutputBuffer(socket, bufferSize, params), params);
        this.open = true;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
    
    protected Socket getSocket() {
        return this.socket;
    }
    
    @Override
    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }
    
    @Override
    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }
    
    @Override
    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        this.assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            }
            catch (SocketException ex) {}
        }
    }
    
    @Override
    public int getSocketTimeout() {
        if (this.socket != null) {
            try {
                return this.socket.getSoTimeout();
            }
            catch (SocketException ignore) {
                return -1;
            }
        }
        return -1;
    }
    
    @Override
    public void shutdown() throws IOException {
        this.open = false;
        final Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.open) {
            return;
        }
        this.open = false;
        final Socket sock = this.socket;
        try {
            this.doFlush();
            try {
                try {
                    sock.shutdownOutput();
                }
                catch (IOException ex) {}
                try {
                    sock.shutdownInput();
                }
                catch (IOException ex2) {}
            }
            catch (UnsupportedOperationException ex3) {}
        }
        finally {
            sock.close();
        }
    }
    
    private static void formatAddress(final StringBuilder buffer, final SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            final InetSocketAddress addr = (InetSocketAddress)socketAddress;
            buffer.append((addr.getAddress() != null) ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
        }
        else {
            buffer.append(socketAddress);
        }
    }
    
    @Override
    public String toString() {
        if (this.socket != null) {
            final StringBuilder buffer = new StringBuilder();
            final SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
            final SocketAddress localAddress = this.socket.getLocalSocketAddress();
            if (remoteAddress != null && localAddress != null) {
                formatAddress(buffer, localAddress);
                buffer.append("<->");
                formatAddress(buffer, remoteAddress);
            }
            return buffer.toString();
        }
        return super.toString();
    }
}
