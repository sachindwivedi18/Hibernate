// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.io;

import rp.org.apache.http.HttpException;
import java.io.IOException;
import rp.org.apache.http.HttpMessage;

public interface HttpMessageWriter<T extends HttpMessage>
{
    void write(final T p0) throws IOException, HttpException;
}
