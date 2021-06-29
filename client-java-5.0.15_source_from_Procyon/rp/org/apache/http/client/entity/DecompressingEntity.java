// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import rp.org.apache.http.Header;
import rp.org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.IOException;
import rp.org.apache.http.HttpEntity;
import java.io.InputStream;
import rp.org.apache.http.entity.HttpEntityWrapper;

public class DecompressingEntity extends HttpEntityWrapper
{
    private static final int BUFFER_SIZE = 2048;
    private final InputStreamFactory inputStreamFactory;
    private InputStream content;
    
    public DecompressingEntity(final HttpEntity wrapped, final InputStreamFactory inputStreamFactory) {
        super(wrapped);
        this.inputStreamFactory = inputStreamFactory;
    }
    
    private InputStream getDecompressingStream() throws IOException {
        final InputStream in = this.wrappedEntity.getContent();
        return new LazyDecompressingInputStream(in, this.inputStreamFactory);
    }
    
    @Override
    public InputStream getContent() throws IOException {
        if (this.wrappedEntity.isStreaming()) {
            if (this.content == null) {
                this.content = this.getDecompressingStream();
            }
            return this.content;
        }
        return this.getDecompressingStream();
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = this.getContent();
        try {
            final byte[] buffer = new byte[2048];
            int l;
            while ((l = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, l);
            }
        }
        finally {
            inStream.close();
        }
    }
    
    @Override
    public Header getContentEncoding() {
        return null;
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
}
