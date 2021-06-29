// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import java.io.IOException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.io.SessionOutputBuffer;
import java.io.OutputStream;

public class IdentityOutputStream extends OutputStream
{
    private final SessionOutputBuffer out;
    private boolean closed;
    
    public IdentityOutputStream(final SessionOutputBuffer out) {
        this.closed = false;
        this.out = Args.notNull(out, "Session output buffer");
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.out.flush();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.out.write(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.out.write(b);
    }
}
