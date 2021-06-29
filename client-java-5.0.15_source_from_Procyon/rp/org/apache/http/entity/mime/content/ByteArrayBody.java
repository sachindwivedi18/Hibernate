// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.ContentType;

public class ByteArrayBody extends AbstractContentBody
{
    private final byte[] data;
    private final String filename;
    
    @Deprecated
    public ByteArrayBody(final byte[] data, final String mimeType, final String filename) {
        this(data, ContentType.create(mimeType), filename);
    }
    
    public ByteArrayBody(final byte[] data, final ContentType contentType, final String filename) {
        super(contentType);
        Args.notNull(data, "byte[]");
        this.data = data;
        this.filename = filename;
    }
    
    public ByteArrayBody(final byte[] data, final String filename) {
        this(data, "application/octet-stream", filename);
    }
    
    @Override
    public String getFilename() {
        return this.filename;
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.data);
    }
    
    @Override
    public String getCharset() {
        return null;
    }
    
    @Override
    public String getTransferEncoding() {
        return "binary";
    }
    
    @Override
    public long getContentLength() {
        return this.data.length;
    }
}
