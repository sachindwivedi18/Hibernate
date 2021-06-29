// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.ContentType;
import rp.org.apache.http.Consts;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringBody extends AbstractContentBody
{
    private final byte[] content;
    
    @Deprecated
    public static StringBody create(final String text, final String mimeType, final Charset charset) throws IllegalArgumentException {
        try {
            return new StringBody(text, mimeType, charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }
    
    @Deprecated
    public static StringBody create(final String text, final Charset charset) throws IllegalArgumentException {
        return create(text, null, charset);
    }
    
    @Deprecated
    public static StringBody create(final String text) throws IllegalArgumentException {
        return create(text, null, null);
    }
    
    @Deprecated
    public StringBody(final String text, final String mimeType, final Charset charset) throws UnsupportedEncodingException {
        this(text, ContentType.create(mimeType, (charset != null) ? charset : Consts.ASCII));
    }
    
    @Deprecated
    public StringBody(final String text, final Charset charset) throws UnsupportedEncodingException {
        this(text, "text/plain", charset);
    }
    
    @Deprecated
    public StringBody(final String text) throws UnsupportedEncodingException {
        this(text, "text/plain", Consts.ASCII);
    }
    
    public StringBody(final String text, final ContentType contentType) {
        super(contentType);
        Args.notNull(text, "Text");
        final Charset charset = contentType.getCharset();
        this.content = text.getBytes((charset != null) ? charset : Consts.ASCII);
    }
    
    public Reader getReader() {
        final Charset charset = this.getContentType().getCharset();
        return new InputStreamReader(new ByteArrayInputStream(this.content), (charset != null) ? charset : Consts.ASCII);
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new ByteArrayInputStream(this.content);
        final byte[] tmp = new byte[4096];
        int l;
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }
        out.flush();
    }
    
    @Override
    public String getTransferEncoding() {
        return "8bit";
    }
    
    @Override
    public long getContentLength() {
        return this.content.length;
    }
    
    @Override
    public String getFilename() {
        return null;
    }
}
