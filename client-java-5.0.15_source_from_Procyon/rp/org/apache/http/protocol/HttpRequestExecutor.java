// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.protocol;

import rp.org.apache.http.ProtocolException;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.HttpEntityEnclosingRequest;
import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.HttpClientConnection;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class HttpRequestExecutor
{
    public static final int DEFAULT_WAIT_FOR_CONTINUE = 3000;
    private final int waitForContinue;
    
    public HttpRequestExecutor(final int waitForContinue) {
        this.waitForContinue = Args.positive(waitForContinue, "Wait for continue time");
    }
    
    public HttpRequestExecutor() {
        this(3000);
    }
    
    protected boolean canResponseHaveBody(final HttpRequest request, final HttpResponse response) {
        if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            return false;
        }
        final int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }
    
    public HttpResponse execute(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        Args.notNull(conn, "Client connection");
        Args.notNull(context, "HTTP context");
        try {
            HttpResponse response = this.doSendRequest(request, conn, context);
            if (response == null) {
                response = this.doReceiveResponse(request, conn, context);
            }
            return response;
        }
        catch (IOException ex) {
            closeConnection(conn);
            throw ex;
        }
        catch (HttpException ex2) {
            closeConnection(conn);
            throw ex2;
        }
        catch (RuntimeException ex3) {
            closeConnection(conn);
            throw ex3;
        }
    }
    
    private static void closeConnection(final HttpClientConnection conn) {
        try {
            conn.close();
        }
        catch (IOException ex) {}
    }
    
    public void preProcess(final HttpRequest request, final HttpProcessor processor, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(processor, "HTTP processor");
        Args.notNull(context, "HTTP context");
        context.setAttribute("http.request", request);
        processor.process(request, context);
    }
    
    protected HttpResponse doSendRequest(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        Args.notNull(conn, "Client connection");
        Args.notNull(context, "HTTP context");
        HttpResponse response = null;
        context.setAttribute("http.connection", conn);
        context.setAttribute("http.request_sent", Boolean.FALSE);
        conn.sendRequestHeader(request);
        if (request instanceof HttpEntityEnclosingRequest) {
            boolean sendentity = true;
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            if (((HttpEntityEnclosingRequest)request).expectContinue() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                conn.flush();
                if (conn.isResponseAvailable(this.waitForContinue)) {
                    response = conn.receiveResponseHeader();
                    if (this.canResponseHaveBody(request, response)) {
                        conn.receiveResponseEntity(response);
                    }
                    final int status = response.getStatusLine().getStatusCode();
                    if (status < 200) {
                        if (status != 100) {
                            throw new ProtocolException("Unexpected response: " + response.getStatusLine());
                        }
                        response = null;
                    }
                    else {
                        sendentity = false;
                    }
                }
            }
            if (sendentity) {
                conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
            }
        }
        conn.flush();
        context.setAttribute("http.request_sent", Boolean.TRUE);
        return response;
    }
    
    protected HttpResponse doReceiveResponse(final HttpRequest request, final HttpClientConnection conn, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(conn, "Client connection");
        Args.notNull(context, "HTTP context");
        HttpResponse response = null;
        int statusCode = 0;
        while (response == null || statusCode < 200) {
            response = conn.receiveResponseHeader();
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 100) {
                throw new ProtocolException("Invalid response: " + response.getStatusLine());
            }
            if (!this.canResponseHaveBody(request, response)) {
                continue;
            }
            conn.receiveResponseEntity(response);
        }
        return response;
    }
    
    public void postProcess(final HttpResponse response, final HttpProcessor processor, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        Args.notNull(processor, "HTTP processor");
        Args.notNull(context, "HTTP context");
        context.setAttribute("http.response", response);
        processor.process(response, context);
    }
}
