// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.StatusLine;
import java.util.Locale;
import rp.org.apache.http.message.BasicHttpResponse;
import rp.org.apache.http.message.BasicStatusLine;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.ReasonPhraseCatalog;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseFactory implements HttpResponseFactory
{
    public static final DefaultHttpResponseFactory INSTANCE;
    protected final ReasonPhraseCatalog reasonCatalog;
    
    public DefaultHttpResponseFactory(final ReasonPhraseCatalog catalog) {
        this.reasonCatalog = Args.notNull(catalog, "Reason phrase catalog");
    }
    
    public DefaultHttpResponseFactory() {
        this(EnglishReasonPhraseCatalog.INSTANCE);
    }
    
    @Override
    public HttpResponse newHttpResponse(final ProtocolVersion ver, final int status, final HttpContext context) {
        Args.notNull(ver, "HTTP version");
        final Locale loc = this.determineLocale(context);
        final String reason = this.reasonCatalog.getReason(status, loc);
        final StatusLine statusline = new BasicStatusLine(ver, status, reason);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }
    
    @Override
    public HttpResponse newHttpResponse(final StatusLine statusline, final HttpContext context) {
        Args.notNull(statusline, "Status line");
        return new BasicHttpResponse(statusline, this.reasonCatalog, this.determineLocale(context));
    }
    
    protected Locale determineLocale(final HttpContext context) {
        return Locale.getDefault();
    }
    
    static {
        INSTANCE = new DefaultHttpResponseFactory();
    }
}
