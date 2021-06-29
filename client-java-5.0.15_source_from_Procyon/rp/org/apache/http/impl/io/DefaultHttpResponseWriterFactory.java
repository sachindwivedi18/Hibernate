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
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.io.HttpMessageWriterFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory<HttpResponse>
{
    public static final DefaultHttpResponseWriterFactory INSTANCE;
    private final LineFormatter lineFormatter;
    
    public DefaultHttpResponseWriterFactory(final LineFormatter lineFormatter) {
        this.lineFormatter = ((lineFormatter != null) ? lineFormatter : BasicLineFormatter.INSTANCE);
    }
    
    public DefaultHttpResponseWriterFactory() {
        this(null);
    }
    
    @Override
    public HttpMessageWriter<HttpResponse> create(final SessionOutputBuffer buffer) {
        return new DefaultHttpResponseWriter(buffer, this.lineFormatter);
    }
    
    static {
        INSTANCE = new DefaultHttpResponseWriterFactory();
    }
}
