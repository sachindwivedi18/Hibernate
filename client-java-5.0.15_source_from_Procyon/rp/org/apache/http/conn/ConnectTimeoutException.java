// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import java.util.Arrays;
import java.net.InetAddress;
import java.io.IOException;
import rp.org.apache.http.HttpHost;
import java.io.InterruptedIOException;

public class ConnectTimeoutException extends InterruptedIOException
{
    private static final long serialVersionUID = -4816682903149535989L;
    private final HttpHost host;
    
    public ConnectTimeoutException() {
        this.host = null;
    }
    
    public ConnectTimeoutException(final String message) {
        super(message);
        this.host = null;
    }
    
    public ConnectTimeoutException(final IOException cause, final HttpHost host, final InetAddress... remoteAddresses) {
        super("Connect to " + ((host != null) ? host.toHostString() : "remote host") + ((remoteAddresses != null && remoteAddresses.length > 0) ? (" " + Arrays.asList(remoteAddresses)) : "") + ((cause != null && cause.getMessage() != null) ? (" failed: " + cause.getMessage()) : " timed out"));
        this.host = host;
        this.initCause(cause);
    }
    
    public HttpHost getHost() {
        return this.host;
    }
}
