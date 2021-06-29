// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import rp.org.apache.http.Header;
import rp.org.apache.http.entity.mime.content.ContentBody;
import java.util.Random;
import java.nio.charset.Charset;
import rp.org.apache.http.HttpEntity;

@Deprecated
public class MultipartEntity implements HttpEntity
{
    private static final char[] MULTIPART_CHARS;
    private final MultipartEntityBuilder builder;
    private volatile MultipartFormEntity entity;
    
    public MultipartEntity(final HttpMultipartMode mode, final String boundary, final Charset charset) {
        this.builder = new MultipartEntityBuilder().setMode(mode).setCharset((charset != null) ? charset : MIME.DEFAULT_CHARSET).setBoundary(boundary);
        this.entity = null;
    }
    
    public MultipartEntity(final HttpMultipartMode mode) {
        this(mode, null, null);
    }
    
    public MultipartEntity() {
        this(HttpMultipartMode.STRICT, null, null);
    }
    
    protected String generateContentType(final String boundary, final Charset charset) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("multipart/form-data; boundary=");
        buffer.append(boundary);
        if (charset != null) {
            buffer.append("; charset=");
            buffer.append(charset.name());
        }
        return buffer.toString();
    }
    
    protected String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        for (int count = rand.nextInt(11) + 30, i = 0; i < count; ++i) {
            buffer.append(MultipartEntity.MULTIPART_CHARS[rand.nextInt(MultipartEntity.MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
    
    private MultipartFormEntity getEntity() {
        if (this.entity == null) {
            this.entity = this.builder.buildEntity();
        }
        return this.entity;
    }
    
    public void addPart(final FormBodyPart bodyPart) {
        this.builder.addPart(bodyPart);
        this.entity = null;
    }
    
    public void addPart(final String name, final ContentBody contentBody) {
        this.addPart(new FormBodyPart(name, contentBody));
    }
    
    @Override
    public boolean isRepeatable() {
        return this.getEntity().isRepeatable();
    }
    
    @Override
    public boolean isChunked() {
        return this.getEntity().isChunked();
    }
    
    @Override
    public boolean isStreaming() {
        return this.getEntity().isStreaming();
    }
    
    @Override
    public long getContentLength() {
        return this.getEntity().getContentLength();
    }
    
    @Override
    public Header getContentType() {
        return this.getEntity().getContentType();
    }
    
    @Override
    public Header getContentEncoding() {
        return this.getEntity().getContentEncoding();
    }
    
    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (this.isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }
    
    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        this.getEntity().writeTo(outStream);
    }
    
    static {
        MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }
}
