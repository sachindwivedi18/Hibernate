// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.conn;

import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.Header;
import rp.org.apache.http.params.BasicHttpParams;
import rp.org.apache.http.message.LineParser;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageParser;
import rp.org.apache.http.HttpResponseFactory;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.params.HttpProtocolParams;
import rp.org.apache.http.io.SessionInputBuffer;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import java.util.HashMap;
import rp.org.apache.commons.logging.LogFactory;
import java.util.Map;
import rp.org.apache.http.HttpHost;
import java.net.Socket;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.conn.ManagedHttpClientConnection;
import rp.org.apache.http.conn.OperatedClientConnection;
import rp.org.apache.http.impl.SocketHttpClientConnection;

@Deprecated
public class DefaultClientConnection extends SocketHttpClientConnection implements OperatedClientConnection, ManagedHttpClientConnection, HttpContext
{
    private final Log log;
    private final Log headerLog;
    private final Log wireLog;
    private volatile Socket socket;
    private HttpHost targetHost;
    private boolean connSecure;
    private volatile boolean shutdown;
    private final Map<String, Object> attributes;
    
    public DefaultClientConnection() {
        this.log = LogFactory.getLog(this.getClass());
        this.headerLog = LogFactory.getLog("rp.org.apache.http.headers");
        this.wireLog = LogFactory.getLog("rp.org.apache.http.wire");
        this.attributes = new HashMap<String, Object>();
    }
    
    @Override
    public String getId() {
        return null;
    }
    
    @Override
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }
    
    @Override
    public final boolean isSecure() {
        return this.connSecure;
    }
    
    @Override
    public final Socket getSocket() {
        return this.socket;
    }
    
    @Override
    public SSLSession getSSLSession() {
        if (this.socket instanceof SSLSocket) {
            return ((SSLSocket)this.socket).getSession();
        }
        return null;
    }
    
    @Override
    public void opening(final Socket sock, final HttpHost target) throws IOException {
        this.assertNotOpen();
        this.socket = sock;
        this.targetHost = target;
        if (this.shutdown) {
            sock.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
    }
    
    @Override
    public void openCompleted(final boolean secure, final HttpParams params) throws IOException {
        Args.notNull(params, "Parameters");
        this.assertNotOpen();
        this.connSecure = secure;
        this.bind(this.socket, params);
    }
    
    @Override
    public void shutdown() throws IOException {
        this.shutdown = true;
        try {
            super.shutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " shut down");
            }
            final Socket sock = this.socket;
            if (sock != null) {
                sock.close();
            }
        }
        catch (IOException ex) {
            this.log.debug("I/O error shutting down connection", ex);
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            super.close();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " closed");
            }
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
    
    @Override
    protected SessionInputBuffer createSessionInputBuffer(final Socket socket, final int bufferSize, final HttpParams params) throws IOException {
        SessionInputBuffer inBuffer = super.createSessionInputBuffer(socket, (bufferSize > 0) ? bufferSize : 8192, params);
        if (this.wireLog.isDebugEnabled()) {
            inBuffer = new LoggingSessionInputBuffer(inBuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return inBuffer;
    }
    
    @Override
    protected SessionOutputBuffer createSessionOutputBuffer(final Socket socket, final int bufferSize, final HttpParams params) throws IOException {
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, (bufferSize > 0) ? bufferSize : 8192, params);
        if (this.wireLog.isDebugEnabled()) {
            outbuffer = new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return outbuffer;
    }
    
    @Override
    protected HttpMessageParser<HttpResponse> createResponseParser(final SessionInputBuffer buffer, final HttpResponseFactory responseFactory, final HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        this.bind(socket, new BasicHttpParams());
    }
    
    @Override
    public void update(final Socket sock, final HttpHost target, final boolean secure, final HttpParams params) throws IOException {
        this.assertOpen();
        Args.notNull(target, "Target host");
        Args.notNull(params, "Parameters");
        if (sock != null) {
            this.bind(this.socket = sock, params);
        }
        this.targetHost = target;
        this.connSecure = secure;
    }
    
    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        final HttpResponse response = super.receiveResponseHeader();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Receiving response: " + response.getStatusLine());
        }
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug("<< " + response.getStatusLine().toString());
            final Header[] arr$;
            final Header[] headers = arr$ = response.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug("<< " + header.toString());
            }
        }
        return response;
    }
    
    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending request: " + request.getRequestLine());
        }
        super.sendRequestHeader(request);
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(">> " + request.getRequestLine().toString());
            final Header[] arr$;
            final Header[] headers = arr$ = request.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug(">> " + header.toString());
            }
        }
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
}
