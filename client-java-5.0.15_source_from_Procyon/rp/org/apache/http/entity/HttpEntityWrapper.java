// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import rp.org.apache.http.Header;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpEntity;

public class HttpEntityWrapper implements HttpEntity
{
    protected HttpEntity wrappedEntity;
    
    public HttpEntityWrapper(final HttpEntity wrappedEntity) {
        this.wrappedEntity = Args.notNull(wrappedEntity, "Wrapped entity");
    }
    
    @Override
    public boolean isRepeatable() {
        return this.wrappedEntity.isRepeatable();
    }
    
    @Override
    public boolean isChunked() {
        return this.wrappedEntity.isChunked();
    }
    
    @Override
    public long getContentLength() {
        return this.wrappedEntity.getContentLength();
    }
    
    @Override
    public Header getContentType() {
        return this.wrappedEntity.getContentType();
    }
    
    @Override
    public Header getContentEncoding() {
        return this.wrappedEntity.getContentEncoding();
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return this.wrappedEntity.getContent();
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        this.wrappedEntity.writeTo(outStream);
    }
    
    @Override
    public boolean isStreaming() {
        return this.wrappedEntity.isStreaming();
    }
    
    @Deprecated
    @Override
    public void consumeContent() throws IOException {
        this.wrappedEntity.consumeContent();
    }
}
