// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.io.HttpTransportMetrics;

public class HttpTransportMetricsImpl implements HttpTransportMetrics
{
    private long bytesTransferred;
    
    public HttpTransportMetricsImpl() {
        this.bytesTransferred = 0L;
    }
    
    @Override
    public long getBytesTransferred() {
        return this.bytesTransferred;
    }
    
    public void setBytesTransferred(final long count) {
        this.bytesTransferred = count;
    }
    
    public void incrementBytesTransferred(final long count) {
        this.bytesTransferred += count;
    }
    
    @Override
    public void reset() {
        this.bytesTransferred = 0L;
    }
}
