// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import java.util.zip.GZIPOutputStream;
import rp.org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import rp.org.apache.http.message.BasicHeader;
import rp.org.apache.http.Header;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.entity.HttpEntityWrapper;

public class GzipCompressingEntity extends HttpEntityWrapper
{
    private static final String GZIP_CODEC = "gzip";
    
    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    @Override
    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "gzip");
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
    
    @Override
    public boolean isChunked() {
        return true;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final GZIPOutputStream gzip = new GZIPOutputStream(outStream);
        this.wrappedEntity.writeTo(gzip);
        gzip.close();
    }
}
