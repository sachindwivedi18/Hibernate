// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.Charset;
import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.util.Args;

public class StringEntity extends AbstractHttpEntity implements Cloneable
{
    protected final byte[] content;
    
    public StringEntity(final String string, final ContentType contentType) throws UnsupportedCharsetException {
        Args.notNull(string, "Source string");
        Charset charset = (contentType != null) ? contentType.getCharset() : null;
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        this.content = string.getBytes(charset);
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    @Deprecated
    public StringEntity(final String string, final String mimeType, final String charset) throws UnsupportedEncodingException {
        Args.notNull(string, "Source string");
        final String mt = (mimeType != null) ? mimeType : "text/plain";
        final String cs = (charset != null) ? charset : "ISO-8859-1";
        this.content = string.getBytes(cs);
        this.setContentType(mt + "; charset=" + cs);
    }
    
    public StringEntity(final String string, final String charset) throws UnsupportedCharsetException {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }
    
    public StringEntity(final String string, final Charset charset) {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }
    
    public StringEntity(final String string) throws UnsupportedEncodingException {
        this(string, ContentType.DEFAULT_TEXT);
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public long getContentLength() {
        return this.content.length;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        outStream.write(this.content);
        outStream.flush();
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
