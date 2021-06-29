// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DeflateInputStreamFactory implements InputStreamFactory
{
    private static final DeflateInputStreamFactory INSTANCE;
    
    public static DeflateInputStreamFactory getInstance() {
        return DeflateInputStreamFactory.INSTANCE;
    }
    
    @Override
    public InputStream create(final InputStream inputStream) throws IOException {
        return new DeflateInputStream(inputStream);
    }
    
    static {
        INSTANCE = new DeflateInputStreamFactory();
    }
}
