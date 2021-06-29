// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import rp.org.apache.http.util.Args;

public class EntityTemplate extends AbstractHttpEntity
{
    private final ContentProducer contentproducer;
    
    public EntityTemplate(final ContentProducer contentproducer) {
        this.contentproducer = Args.notNull(contentproducer, "Content producer");
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        this.writeTo(buf);
        return new ByteArrayInputStream(buf.toByteArray());
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        this.contentproducer.writeTo(outStream);
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
}
