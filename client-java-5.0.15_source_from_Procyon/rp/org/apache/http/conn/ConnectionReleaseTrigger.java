// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.conn;

import java.io.IOException;

public interface ConnectionReleaseTrigger
{
    void releaseConnection() throws IOException;
    
    void abortConnection() throws IOException;
}
