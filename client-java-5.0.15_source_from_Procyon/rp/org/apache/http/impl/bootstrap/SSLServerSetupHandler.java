// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.bootstrap;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

public interface SSLServerSetupHandler
{
    void initialize(final SSLServerSocket p0) throws SSLException;
}
