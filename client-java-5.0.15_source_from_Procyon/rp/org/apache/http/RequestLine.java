// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

public interface RequestLine
{
    String getMethod();
    
    ProtocolVersion getProtocolVersion();
    
    String getUri();
}
