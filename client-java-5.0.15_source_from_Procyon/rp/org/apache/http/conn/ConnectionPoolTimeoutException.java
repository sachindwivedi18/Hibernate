// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

public class ConnectionPoolTimeoutException extends ConnectTimeoutException
{
    private static final long serialVersionUID = -7898874842020245128L;
    
    public ConnectionPoolTimeoutException() {
    }
    
    public ConnectionPoolTimeoutException(final String message) {
        super(message);
    }
}
