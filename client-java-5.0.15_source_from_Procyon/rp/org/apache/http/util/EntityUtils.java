// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.util;

import java.nio.charset.UnsupportedCharsetException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.entity.ContentType;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.NameValuePair;
import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.HttpResponse;
import java.io.InputStream;
import java.io.IOException;
import rp.org.apache.http.HttpEntity;

public final class EntityUtils
{
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    private EntityUtils() {
    }
    
    public static void consumeQuietly(final HttpEntity entity) {
        try {
            consume(entity);
        }
        catch (IOException ex) {}
    }
    
    public static void consume(final HttpEntity entity) throws IOException {
        if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
            final InputStream inStream = entity.getContent();
            if (inStream != null) {
                inStream.close();
            }
        }
    }
    
    public static void updateEntity(final HttpResponse response, final HttpEntity entity) throws IOException {
        Args.notNull(response, "Response");
        consume(response.getEntity());
        response.setEntity(entity);
    }
    
    public static byte[] toByteArray(final HttpEntity entity) throws IOException {
        Args.notNull(entity, "Entity");
        final InputStream inStream = entity.getContent();
        if (inStream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= 2147483647L, "HTTP entity too large to be buffered in memory");
            int capacity = (int)entity.getContentLength();
            if (capacity < 0) {
                capacity = 4096;
            }
            final ByteArrayBuffer buffer = new ByteArrayBuffer(capacity);
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = inStream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        }
        finally {
            inStream.close();
        }
    }
    
    @Deprecated
    public static String getContentCharSet(final HttpEntity entity) throws ParseException {
        Args.notNull(entity, "Entity");
        String charset = null;
        if (entity.getContentType() != null) {
            final HeaderElement[] values = entity.getContentType().getElements();
            if (values.length > 0) {
                final NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }
        return charset;
    }
    
    @Deprecated
    public static String getContentMimeType(final HttpEntity entity) throws ParseException {
        Args.notNull(entity, "Entity");
        String mimeType = null;
        if (entity.getContentType() != null) {
            final HeaderElement[] values = entity.getContentType().getElements();
            if (values.length > 0) {
                mimeType = values[0].getName();
            }
        }
        return mimeType;
    }
    
    private static String toString(final HttpEntity entity, final ContentType contentType) throws IOException {
        final InputStream inStream = entity.getContent();
        if (inStream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= 2147483647L, "HTTP entity too large to be buffered in memory");
            int capacity = (int)entity.getContentLength();
            if (capacity < 0) {
                capacity = 4096;
            }
            Charset charset = null;
            if (contentType != null) {
                charset = contentType.getCharset();
                if (charset == null) {
                    final ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
                    charset = ((defaultContentType != null) ? defaultContentType.getCharset() : null);
                }
            }
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            final Reader reader = new InputStreamReader(inStream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(capacity);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        }
        finally {
            inStream.close();
        }
    }
    
    public static String toString(final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        ContentType contentType = null;
        try {
            contentType = ContentType.get(entity);
        }
        catch (UnsupportedCharsetException ex) {
            if (defaultCharset == null) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
        }
        if (contentType != null) {
            if (contentType.getCharset() == null) {
                contentType = contentType.withCharset(defaultCharset);
            }
        }
        else {
            contentType = ContentType.DEFAULT_TEXT.withCharset(defaultCharset);
        }
        return toString(entity, contentType);
    }
    
    public static String toString(final HttpEntity entity, final String defaultCharset) throws IOException, ParseException {
        return toString(entity, (defaultCharset != null) ? Charset.forName(defaultCharset) : null);
    }
    
    public static String toString(final HttpEntity entity) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        return toString(entity, ContentType.get(entity));
    }
}
