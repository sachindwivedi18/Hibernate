// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.protocol;

import java.io.IOException;
import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.Header;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.client.entity.DecompressingEntity;
import java.util.Locale;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.entity.DeflateInputStreamFactory;
import rp.org.apache.http.client.entity.GZIPInputStreamFactory;
import rp.org.apache.http.config.RegistryBuilder;
import rp.org.apache.http.client.entity.InputStreamFactory;
import rp.org.apache.http.config.Lookup;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ResponseContentEncoding implements HttpResponseInterceptor
{
    public static final String UNCOMPRESSED = "http.client.response.uncompressed";
    private final Lookup<InputStreamFactory> decoderRegistry;
    private final boolean ignoreUnknown;
    
    public ResponseContentEncoding(final Lookup<InputStreamFactory> decoderRegistry, final boolean ignoreUnknown) {
        this.decoderRegistry = (Lookup<InputStreamFactory>)((decoderRegistry != null) ? decoderRegistry : RegistryBuilder.create().register("gzip", GZIPInputStreamFactory.getInstance()).register("x-gzip", GZIPInputStreamFactory.getInstance()).register("deflate", (GZIPInputStreamFactory)DeflateInputStreamFactory.getInstance()).build());
        this.ignoreUnknown = ignoreUnknown;
    }
    
    public ResponseContentEncoding(final boolean ignoreUnknown) {
        this(null, ignoreUnknown);
    }
    
    public ResponseContentEncoding(final Lookup<InputStreamFactory> decoderRegistry) {
        this(decoderRegistry, true);
    }
    
    public ResponseContentEncoding() {
        this(null);
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        final HttpEntity entity = response.getEntity();
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig requestConfig = clientContext.getRequestConfig();
        if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0L) {
            final Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                final HeaderElement[] arr$;
                final HeaderElement[] codecs = arr$ = ceheader.getElements();
                for (final HeaderElement codec : arr$) {
                    final String codecname = codec.getName().toLowerCase(Locale.ROOT);
                    final InputStreamFactory decoderFactory = this.decoderRegistry.lookup(codecname);
                    if (decoderFactory != null) {
                        response.setEntity(new DecompressingEntity(response.getEntity(), decoderFactory));
                        response.removeHeaders("Content-Length");
                        response.removeHeaders("Content-Encoding");
                        response.removeHeaders("Content-MD5");
                    }
                    else if (!"identity".equals(codecname) && !this.ignoreUnknown) {
                        throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
                    }
                }
            }
        }
    }
}
