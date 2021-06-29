// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

public interface StatusLine
{
    ProtocolVersion getProtocolVersion();
    
    int getStatusCode();
    
    String getReasonPhrase();
}
