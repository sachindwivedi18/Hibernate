// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import java.io.IOException;
import rp.org.apache.http.io.BufferInfo;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.io.SessionInputBuffer;
import java.io.InputStream;

public class IdentityInputStream extends InputStream
{
    private final SessionInputBuffer in;
    private boolean closed;
    
    public IdentityInputStream(final SessionInputBuffer in) {
        this.closed = false;
        this.in = Args.notNull(in, "Session input buffer");
    }
    
    @Override
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            return ((BufferInfo)this.in).length();
        }
        return 0;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
    }
    
    @Override
    public int read() throws IOException {
        return this.closed ? -1 : this.in.read();
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.closed ? -1 : this.in.read(b, off, len);
    }
}
