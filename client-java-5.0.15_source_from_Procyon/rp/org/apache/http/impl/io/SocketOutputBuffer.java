// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import java.io.IOException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.params.HttpParams;
import java.net.Socket;

@Deprecated
public class SocketOutputBuffer extends AbstractSessionOutputBuffer
{
    public SocketOutputBuffer(final Socket socket, final int bufferSize, final HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        int n = bufferSize;
        if (n < 0) {
            n = socket.getSendBufferSize();
        }
        if (n < 1024) {
            n = 1024;
        }
        this.init(socket.getOutputStream(), n, params);
    }
}
