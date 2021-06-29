// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.io;

public interface HttpTransportMetrics
{
    long getBytesTransferred();
    
    void reset();
}
