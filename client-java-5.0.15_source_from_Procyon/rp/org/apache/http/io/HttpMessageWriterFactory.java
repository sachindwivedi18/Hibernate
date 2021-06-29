// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.io;

import rp.org.apache.http.HttpMessage;

public interface HttpMessageWriterFactory<T extends HttpMessage>
{
    HttpMessageWriter<T> create(final SessionOutputBuffer p0);
}
