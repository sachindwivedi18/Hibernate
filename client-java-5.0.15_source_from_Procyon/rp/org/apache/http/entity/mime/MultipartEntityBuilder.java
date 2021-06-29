// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import rp.org.apache.http.HttpEntity;
import java.util.Collections;
import java.util.Collection;
import rp.org.apache.http.message.BasicNameValuePair;
import rp.org.apache.http.NameValuePair;
import java.util.Random;
import rp.org.apache.http.entity.mime.content.InputStreamBody;
import java.io.InputStream;
import rp.org.apache.http.entity.mime.content.FileBody;
import java.io.File;
import rp.org.apache.http.entity.mime.content.ByteArrayBody;
import rp.org.apache.http.entity.mime.content.StringBody;
import rp.org.apache.http.entity.mime.content.ContentBody;
import java.util.ArrayList;
import rp.org.apache.http.util.Args;
import java.util.List;
import java.nio.charset.Charset;
import rp.org.apache.http.entity.ContentType;

public class MultipartEntityBuilder
{
    private static final char[] MULTIPART_CHARS;
    private static final String DEFAULT_SUBTYPE = "form-data";
    private ContentType contentType;
    private HttpMultipartMode mode;
    private String boundary;
    private Charset charset;
    private List<FormBodyPart> bodyParts;
    
    public static MultipartEntityBuilder create() {
        return new MultipartEntityBuilder();
    }
    
    MultipartEntityBuilder() {
        this.mode = HttpMultipartMode.STRICT;
        this.boundary = null;
        this.charset = null;
        this.bodyParts = null;
    }
    
    public MultipartEntityBuilder setMode(final HttpMultipartMode mode) {
        this.mode = mode;
        return this;
    }
    
    public MultipartEntityBuilder setLaxMode() {
        this.mode = HttpMultipartMode.BROWSER_COMPATIBLE;
        return this;
    }
    
    public MultipartEntityBuilder setStrictMode() {
        this.mode = HttpMultipartMode.STRICT;
        return this;
    }
    
    public MultipartEntityBuilder setBoundary(final String boundary) {
        this.boundary = boundary;
        return this;
    }
    
    public MultipartEntityBuilder setMimeSubtype(final String subType) {
        Args.notBlank(subType, "MIME subtype");
        this.contentType = ContentType.create("multipart/" + subType);
        return this;
    }
    
    @Deprecated
    public MultipartEntityBuilder seContentType(final ContentType contentType) {
        return this.setContentType(contentType);
    }
    
    public MultipartEntityBuilder setContentType(final ContentType contentType) {
        Args.notNull(contentType, "Content type");
        this.contentType = contentType;
        return this;
    }
    
    public MultipartEntityBuilder setCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }
    
    public MultipartEntityBuilder addPart(final FormBodyPart bodyPart) {
        if (bodyPart == null) {
            return this;
        }
        if (this.bodyParts == null) {
            this.bodyParts = new ArrayList<FormBodyPart>();
        }
        this.bodyParts.add(bodyPart);
        return this;
    }
    
    public MultipartEntityBuilder addPart(final String name, final ContentBody contentBody) {
        Args.notNull(name, "Name");
        Args.notNull(contentBody, "Content body");
        return this.addPart(FormBodyPartBuilder.create(name, contentBody).build());
    }
    
    public MultipartEntityBuilder addTextBody(final String name, final String text, final ContentType contentType) {
        return this.addPart(name, new StringBody(text, contentType));
    }
    
    public MultipartEntityBuilder addTextBody(final String name, final String text) {
        return this.addTextBody(name, text, ContentType.DEFAULT_TEXT);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final byte[] b, final ContentType contentType, final String filename) {
        return this.addPart(name, new ByteArrayBody(b, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final byte[] b) {
        return this.addBinaryBody(name, b, ContentType.DEFAULT_BINARY, null);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final File file, final ContentType contentType, final String filename) {
        return this.addPart(name, new FileBody(file, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final File file) {
        return this.addBinaryBody(name, file, ContentType.DEFAULT_BINARY, (file != null) ? file.getName() : null);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final InputStream stream, final ContentType contentType, final String filename) {
        return this.addPart(name, new InputStreamBody(stream, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final InputStream stream) {
        return this.addBinaryBody(name, stream, ContentType.DEFAULT_BINARY, null);
    }
    
    private String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        for (int count = rand.nextInt(11) + 30, i = 0; i < count; ++i) {
            buffer.append(MultipartEntityBuilder.MULTIPART_CHARS[rand.nextInt(MultipartEntityBuilder.MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
    
    MultipartFormEntity buildEntity() {
        String boundaryCopy = this.boundary;
        if (boundaryCopy == null && this.contentType != null) {
            boundaryCopy = this.contentType.getParameter("boundary");
        }
        if (boundaryCopy == null) {
            boundaryCopy = this.generateBoundary();
        }
        Charset charsetCopy = this.charset;
        if (charsetCopy == null && this.contentType != null) {
            charsetCopy = this.contentType.getCharset();
        }
        final List<NameValuePair> paramsList = new ArrayList<NameValuePair>(2);
        paramsList.add(new BasicNameValuePair("boundary", boundaryCopy));
        if (charsetCopy != null) {
            paramsList.add(new BasicNameValuePair("charset", charsetCopy.name()));
        }
        final NameValuePair[] params = paramsList.toArray(new NameValuePair[paramsList.size()]);
        final ContentType contentTypeCopy = (this.contentType != null) ? this.contentType.withParameters(params) : ContentType.create("multipart/form-data", params);
        final List<FormBodyPart> bodyPartsCopy = (this.bodyParts != null) ? new ArrayList<FormBodyPart>(this.bodyParts) : Collections.emptyList();
        final HttpMultipartMode modeCopy = (this.mode != null) ? this.mode : HttpMultipartMode.STRICT;
        AbstractMultipartForm form = null;
        switch (modeCopy) {
            case BROWSER_COMPATIBLE: {
                form = new HttpBrowserCompatibleMultipart(charsetCopy, boundaryCopy, bodyPartsCopy);
                break;
            }
            case RFC6532: {
                form = new HttpRFC6532Multipart(charsetCopy, boundaryCopy, bodyPartsCopy);
                break;
            }
            default: {
                form = new HttpStrictMultipart(charsetCopy, boundaryCopy, bodyPartsCopy);
                break;
            }
        }
        return new MultipartFormEntity(form, contentTypeCopy, form.getTotalLength());
    }
    
    public HttpEntity build() {
        return this.buildEntity();
    }
    
    static {
        MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }
}
