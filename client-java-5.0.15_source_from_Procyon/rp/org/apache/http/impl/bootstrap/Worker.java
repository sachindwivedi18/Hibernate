// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.bootstrap;

import java.io.IOException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.protocol.HttpCoreContext;
import rp.org.apache.http.protocol.BasicHttpContext;
import rp.org.apache.http.ExceptionLogger;
import rp.org.apache.http.HttpServerConnection;
import rp.org.apache.http.protocol.HttpService;

class Worker implements Runnable
{
    private final HttpService httpservice;
    private final HttpServerConnection conn;
    private final ExceptionLogger exceptionLogger;
    
    Worker(final HttpService httpservice, final HttpServerConnection conn, final ExceptionLogger exceptionLogger) {
        this.httpservice = httpservice;
        this.conn = conn;
        this.exceptionLogger = exceptionLogger;
    }
    
    public HttpServerConnection getConnection() {
        return this.conn;
    }
    
    @Override
    public void run() {
        try {
            final BasicHttpContext localContext = new BasicHttpContext();
            final HttpCoreContext context = HttpCoreContext.adapt(localContext);
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
                localContext.clear();
            }
            this.conn.close();
        }
        catch (Exception ex) {
            this.exceptionLogger.log(ex);
            try {
                this.conn.shutdown();
            }
            catch (IOException ex2) {
                this.exceptionLogger.log(ex2);
            }
        }
        finally {
            try {
                this.conn.shutdown();
            }
            catch (IOException ex3) {
                this.exceptionLogger.log(ex3);
            }
        }
    }
}
