// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import java.net.SocketAddress;
import rp.org.apache.http.util.NetUtils;
import rp.org.apache.http.HttpConnectionMetrics;
import java.net.SocketTimeoutException;
import java.net.SocketException;
import java.net.InetAddress;
import rp.org.apache.http.Header;
import rp.org.apache.http.entity.BasicHttpEntity;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.impl.io.ContentLengthInputStream;
import rp.org.apache.http.impl.io.EmptyInputStream;
import rp.org.apache.http.impl.io.IdentityInputStream;
import rp.org.apache.http.impl.io.ChunkedInputStream;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpMessage;
import rp.org.apache.http.impl.io.ContentLengthOutputStream;
import rp.org.apache.http.impl.io.IdentityOutputStream;
import rp.org.apache.http.impl.io.ChunkedOutputStream;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.io.SessionInputBuffer;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import rp.org.apache.http.ConnectionClosedException;
import rp.org.apache.http.impl.entity.StrictContentLengthStrategy;
import rp.org.apache.http.impl.entity.LaxContentLengthStrategy;
import rp.org.apache.http.io.HttpTransportMetrics;
import rp.org.apache.http.impl.io.HttpTransportMetricsImpl;
import rp.org.apache.http.util.Args;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.impl.io.SessionOutputBufferImpl;
import rp.org.apache.http.impl.io.SessionInputBufferImpl;
import rp.org.apache.http.HttpInetConnection;

public class BHttpConnectionBase implements HttpInetConnection
{
    private final SessionInputBufferImpl inBuffer;
    private final SessionOutputBufferImpl outbuffer;
    private final MessageConstraints messageConstraints;
    private final HttpConnectionMetricsImpl connMetrics;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final AtomicReference<Socket> socketHolder;
    
    protected BHttpConnectionBase(final int bufferSize, final int fragmentSizeHint, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints messageConstraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy) {
        Args.positive(bufferSize, "Buffer size");
        final HttpTransportMetricsImpl inTransportMetrics = new HttpTransportMetricsImpl();
        final HttpTransportMetricsImpl outTransportMetrics = new HttpTransportMetricsImpl();
        this.inBuffer = new SessionInputBufferImpl(inTransportMetrics, bufferSize, -1, (messageConstraints != null) ? messageConstraints : MessageConstraints.DEFAULT, charDecoder);
        this.outbuffer = new SessionOutputBufferImpl(outTransportMetrics, bufferSize, fragmentSizeHint, charEncoder);
        this.messageConstraints = messageConstraints;
        this.connMetrics = new HttpConnectionMetricsImpl(inTransportMetrics, outTransportMetrics);
        this.incomingContentStrategy = ((incomingContentStrategy != null) ? incomingContentStrategy : LaxContentLengthStrategy.INSTANCE);
        this.outgoingContentStrategy = ((outgoingContentStrategy != null) ? outgoingContentStrategy : StrictContentLengthStrategy.INSTANCE);
        this.socketHolder = new AtomicReference<Socket>();
    }
    
    protected void ensureOpen() throws IOException {
        final Socket socket = this.socketHolder.get();
        if (socket == null) {
            throw new ConnectionClosedException();
        }
        if (!this.inBuffer.isBound()) {
            this.inBuffer.bind(this.getSocketInputStream(socket));
        }
        if (!this.outbuffer.isBound()) {
            this.outbuffer.bind(this.getSocketOutputStream(socket));
        }
    }
    
    protected InputStream getSocketInputStream(final Socket socket) throws IOException {
        return socket.getInputStream();
    }
    
    protected OutputStream getSocketOutputStream(final Socket socket) throws IOException {
        return socket.getOutputStream();
    }
    
    protected void bind(final Socket socket) throws IOException {
        Args.notNull(socket, "Socket");
        this.socketHolder.set(socket);
        this.inBuffer.bind(null);
        this.outbuffer.bind(null);
    }
    
    protected SessionInputBuffer getSessionInputBuffer() {
        return this.inBuffer;
    }
    
    protected SessionOutputBuffer getSessionOutputBuffer() {
        return this.outbuffer;
    }
    
    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }
    
    @Override
    public boolean isOpen() {
        return this.socketHolder.get() != null;
    }
    
    protected Socket getSocket() {
        return this.socketHolder.get();
    }
    
    protected OutputStream createOutputStream(final long len, final SessionOutputBuffer outbuffer) {
        if (len == -2L) {
            return new ChunkedOutputStream(2048, outbuffer);
        }
        if (len == -1L) {
            return new IdentityOutputStream(outbuffer);
        }
        return new ContentLengthOutputStream(outbuffer, len);
    }
    
    protected OutputStream prepareOutput(final HttpMessage message) throws HttpException {
        final long len = this.outgoingContentStrategy.determineLength(message);
        return this.createOutputStream(len, this.outbuffer);
    }
    
    protected InputStream createInputStream(final long len, final SessionInputBuffer inBuffer) {
        if (len == -2L) {
            return new ChunkedInputStream(inBuffer, this.messageConstraints);
        }
        if (len == -1L) {
            return new IdentityInputStream(inBuffer);
        }
        if (len == 0L) {
            return EmptyInputStream.INSTANCE;
        }
        return new ContentLengthInputStream(inBuffer, len);
    }
    
    protected HttpEntity prepareInput(final HttpMessage message) throws HttpException {
        final BasicHttpEntity entity = new BasicHttpEntity();
        final long len = this.incomingContentStrategy.determineLength(message);
        final InputStream inStream = this.createInputStream(len, this.inBuffer);
        if (len == -2L) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
            entity.setContent(inStream);
        }
        else if (len == -1L) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
            entity.setContent(inStream);
        }
        else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(inStream);
        }
        final Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        final Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }
    
    @Override
    public InetAddress getLocalAddress() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getLocalAddress() : null;
    }
    
    @Override
    public int getLocalPort() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getLocalPort() : -1;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getInetAddress() : null;
    }
    
    @Override
    public int getRemotePort() {
        final Socket socket = this.socketHolder.get();
        return (socket != null) ? socket.getPort() : -1;
    }
    
    @Override
    public void setSocketTimeout(final int timeout) {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            try {
                socket.setSoTimeout(timeout);
            }
            catch (SocketException ex) {}
        }
    }
    
    @Override
    public int getSocketTimeout() {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            try {
                return socket.getSoTimeout();
            }
            catch (SocketException ignore) {
                return -1;
            }
        }
        return -1;
    }
    
    @Override
    public void shutdown() throws IOException {
        final Socket socket = this.socketHolder.getAndSet(null);
        if (socket != null) {
            try {
                socket.setSoLinger(true, 0);
            }
            catch (IOException ex) {}
            finally {
                socket.close();
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        final Socket socket = this.socketHolder.getAndSet(null);
        if (socket != null) {
            try {
                this.inBuffer.clear();
                this.outbuffer.flush();
                try {
                    try {
                        socket.shutdownOutput();
                    }
                    catch (IOException ex) {}
                    try {
                        socket.shutdownInput();
                    }
                    catch (IOException ex2) {}
                }
                catch (UnsupportedOperationException ex3) {}
            }
            finally {
                socket.close();
            }
        }
    }
    
    private int fillInputBuffer(final int timeout) throws IOException {
        final Socket socket = this.socketHolder.get();
        final int oldtimeout = socket.getSoTimeout();
        try {
            socket.setSoTimeout(timeout);
            return this.inBuffer.fillBuffer();
        }
        finally {
            socket.setSoTimeout(oldtimeout);
        }
    }
    
    protected boolean awaitInput(final int timeout) throws IOException {
        if (this.inBuffer.hasBufferedData()) {
            return true;
        }
        this.fillInputBuffer(timeout);
        return this.inBuffer.hasBufferedData();
    }
    
    @Override
    public boolean isStale() {
        if (!this.isOpen()) {
            return true;
        }
        try {
            final int bytesRead = this.fillInputBuffer(1);
            return bytesRead < 0;
        }
        catch (SocketTimeoutException ex) {
            return false;
        }
        catch (IOException ex2) {
            return true;
        }
    }
    
    protected void incrementRequestCount() {
        this.connMetrics.incrementRequestCount();
    }
    
    protected void incrementResponseCount() {
        this.connMetrics.incrementResponseCount();
    }
    
    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.connMetrics;
    }
    
    @Override
    public String toString() {
        final Socket socket = this.socketHolder.get();
        if (socket != null) {
            final StringBuilder buffer = new StringBuilder();
            final SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            final SocketAddress localAddress = socket.getLocalSocketAddress();
            if (remoteAddress != null && localAddress != null) {
                NetUtils.formatAddress(buffer, localAddress);
                buffer.append("<->");
                NetUtils.formatAddress(buffer, remoteAddress);
            }
            return buffer.toString();
        }
        return "[Not bound]";
    }
}
