// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.io;

import rp.org.apache.http.io.HttpMessageWriter;
import rp.org.apache.http.io.SessionOutputBuffer;
import rp.org.apache.http.message.BasicLineFormatter;
import rp.org.apache.http.message.LineFormatter;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.io.HttpMessageWriterFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpRequestWriterFactory implements HttpMessageWriterFactory<HttpRequest>
{
    public static final DefaultHttpRequestWriterFactory INSTANCE;
    private final LineFormatter lineFormatter;
    
    public DefaultHttpRequestWriterFactory(final LineFormatter lineFormatter) {
        this.lineFormatter = ((lineFormatter != null) ? lineFormatter : BasicLineFormatter.INSTANCE);
    }
    
    public DefaultHttpRequestWriterFactory() {
        this(null);
    }
    
    @Override
    public HttpMessageWriter<HttpRequest> create(final SessionOutputBuffer buffer) {
        return new DefaultHttpRequestWriter(buffer, this.lineFormatter);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestWriterFactory();
    }
}
