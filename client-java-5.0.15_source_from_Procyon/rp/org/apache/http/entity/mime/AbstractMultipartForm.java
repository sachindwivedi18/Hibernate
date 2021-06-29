// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import rp.org.apache.http.entity.mime.content.ContentBody;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import rp.org.apache.http.util.Args;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import rp.org.apache.http.util.ByteArrayBuffer;

abstract class AbstractMultipartForm
{
    private static final ByteArrayBuffer FIELD_SEP;
    private static final ByteArrayBuffer CR_LF;
    private static final ByteArrayBuffer TWO_DASHES;
    final Charset charset;
    final String boundary;
    
    private static ByteArrayBuffer encode(final Charset charset, final String string) {
        final ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        final ByteArrayBuffer bab = new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.position(), encoded.remaining());
        return bab;
    }
    
    private static void writeBytes(final ByteArrayBuffer b, final OutputStream out) throws IOException {
        out.write(b.buffer(), 0, b.length());
    }
    
    private static void writeBytes(final String s, final Charset charset, final OutputStream out) throws IOException {
        final ByteArrayBuffer b = encode(charset, s);
        writeBytes(b, out);
    }
    
    private static void writeBytes(final String s, final OutputStream out) throws IOException {
        final ByteArrayBuffer b = encode(MIME.DEFAULT_CHARSET, s);
        writeBytes(b, out);
    }
    
    protected static void writeField(final MinimalField field, final OutputStream out) throws IOException {
        writeBytes(field.getName(), out);
        writeBytes(AbstractMultipartForm.FIELD_SEP, out);
        writeBytes(field.getBody(), out);
        writeBytes(AbstractMultipartForm.CR_LF, out);
    }
    
    protected static void writeField(final MinimalField field, final Charset charset, final OutputStream out) throws IOException {
        writeBytes(field.getName(), charset, out);
        writeBytes(AbstractMultipartForm.FIELD_SEP, out);
        writeBytes(field.getBody(), charset, out);
        writeBytes(AbstractMultipartForm.CR_LF, out);
    }
    
    public AbstractMultipartForm(final Charset charset, final String boundary) {
        Args.notNull(boundary, "Multipart boundary");
        this.charset = ((charset != null) ? charset : MIME.DEFAULT_CHARSET);
        this.boundary = boundary;
    }
    
    public AbstractMultipartForm(final String boundary) {
        this(null, boundary);
    }
    
    public abstract List<FormBodyPart> getBodyParts();
    
    void doWriteTo(final OutputStream out, final boolean writeContent) throws IOException {
        final ByteArrayBuffer boundaryEncoded = encode(this.charset, this.boundary);
        for (final FormBodyPart part : this.getBodyParts()) {
            writeBytes(AbstractMultipartForm.TWO_DASHES, out);
            writeBytes(boundaryEncoded, out);
            writeBytes(AbstractMultipartForm.CR_LF, out);
            this.formatMultipartHeader(part, out);
            writeBytes(AbstractMultipartForm.CR_LF, out);
            if (writeContent) {
                part.getBody().writeTo(out);
            }
            writeBytes(AbstractMultipartForm.CR_LF, out);
        }
        writeBytes(AbstractMultipartForm.TWO_DASHES, out);
        writeBytes(boundaryEncoded, out);
        writeBytes(AbstractMultipartForm.TWO_DASHES, out);
        writeBytes(AbstractMultipartForm.CR_LF, out);
    }
    
    protected abstract void formatMultipartHeader(final FormBodyPart p0, final OutputStream p1) throws IOException;
    
    public void writeTo(final OutputStream out) throws IOException {
        this.doWriteTo(out, true);
    }
    
    public long getTotalLength() {
        long contentLen = 0L;
        for (final FormBodyPart part : this.getBodyParts()) {
            final ContentBody body = part.getBody();
            final long len = body.getContentLength();
            if (len < 0L) {
                return -1L;
            }
            contentLen += len;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            this.doWriteTo(out, false);
            final byte[] extra = out.toByteArray();
            return contentLen + extra.length;
        }
        catch (IOException ex) {
            return -1L;
        }
    }
    
    static {
        FIELD_SEP = encode(MIME.DEFAULT_CHARSET, ": ");
        CR_LF = encode(MIME.DEFAULT_CHARSET, "\r\n");
        TWO_DASHES = encode(MIME.DEFAULT_CHARSET, "--");
    }
}
