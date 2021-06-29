// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class GZIPInputStreamFactory implements InputStreamFactory
{
    private static final GZIPInputStreamFactory INSTANCE;
    
    public static GZIPInputStreamFactory getInstance() {
        return GZIPInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }
    
    static {
        INSTANCE = new GZIPInputStreamFactory();
    }
}
