// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

import java.net.InetAddress;

public interface HttpInetConnection extends HttpConnection
{
    InetAddress getLocalAddress();
    
    int getLocalPort();
    
    InetAddress getRemoteAddress();
    
    int getRemotePort();
}
