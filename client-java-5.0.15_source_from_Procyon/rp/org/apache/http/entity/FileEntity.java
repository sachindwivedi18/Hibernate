// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import rp.org.apache.http.util.Args;
import java.io.File;

public class FileEntity extends AbstractHttpEntity implements Cloneable
{
    protected final File file;
    
    @Deprecated
    public FileEntity(final File file, final String contentType) {
        this.file = Args.notNull(file, "File");
        this.setContentType(contentType);
    }
    
    public FileEntity(final File file, final ContentType contentType) {
        this.file = Args.notNull(file, "File");
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public FileEntity(final File file) {
        this.file = Args.notNull(file, "File");
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public long getContentLength() {
        return this.file.length();
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return new FileInputStream(this.file);
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, l);
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
