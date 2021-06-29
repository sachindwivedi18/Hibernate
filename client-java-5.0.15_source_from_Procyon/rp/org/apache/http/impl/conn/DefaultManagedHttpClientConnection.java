// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParserFactory;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageWriterFactory;
import rp.org.apache.http.entity.ContentLengthStrategy;
import rp.org.apache.http.config.MessageConstraints;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.impl.DefaultBHttpClientConnection;

public class DefaultManagedHttpClientConnection extends DefaultBHttpClientConnection implements ManagedHttpClientConnection, HttpContext
{
    private final String id;
    private final Map<String, Object> attributes;
    private volatile boolean shutdown;
    
    public DefaultManagedHttpClientConnection(final String id, final int bufferSize, final int fragmentSizeHint, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.id = id;
        this.attributes = new ConcurrentHashMap<String, Object>();
    }
    
    public DefaultManagedHttpClientConnection(final String id, final int bufferSize) {
        this(id, bufferSize, bufferSize, null, null, null, null, null, null, null);
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void shutdown() throws IOException {
        this.shutdown = true;
        super.shutdown();
    }
    
    @Override
    public Object getAttribute(final String id) {
        return this.attributes.get(id);
    }
    
    @Override
    public Object removeAttribute(final String id) {
        return this.attributes.remove(id);
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        this.attributes.put(id, obj);
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        if (this.shutdown) {
            socket.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
        super.bind(socket);
    }
    
    @Override
    public Socket getSocket() {
        return super.getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        final Socket socket = super.getSocket();
        return (socket instanceof SSLSocket) ? ((SSLSocket)socket).getSession() : null;
    }
}
