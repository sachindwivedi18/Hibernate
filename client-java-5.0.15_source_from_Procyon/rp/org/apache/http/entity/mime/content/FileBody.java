// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime.content;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.ContentType;
import java.io.File;

public class FileBody extends AbstractContentBody
{
    private final File file;
    private final String filename;
    
    @Deprecated
    public FileBody(final File file, final String filename, final String mimeType, final String charset) {
        this(file, ContentType.create(mimeType, charset), filename);
    }
    
    @Deprecated
    public FileBody(final File file, final String mimeType, final String charset) {
        this(file, null, mimeType, charset);
    }
    
    @Deprecated
    public FileBody(final File file, final String mimeType) {
        this(file, ContentType.create(mimeType), null);
    }
    
    public FileBody(final File file) {
        this(file, ContentType.DEFAULT_BINARY, (file != null) ? file.getName() : null);
    }
    
    public FileBody(final File file, final ContentType contentType, final String filename) {
        super(contentType);
        Args.notNull(file, "File");
        this.file = file;
        this.filename = ((filename == null) ? file.getName() : filename);
    }
    
    public FileBody(final File file, final ContentType contentType) {
        this(file, contentType, (file != null) ? file.getName() : null);
    }
    
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        }
        finally {
            in.close();
        }
    }
    
    @Override
    public String getTransferEncoding() {
        return "binary";
    }
    
    @Override
    public long getContentLength() {
        return this.file.length();
    }
    
    @Override
    public String getFilename() {
        return this.filename;
    }
    
    public File getFile() {
        return this.file;
    }
}
