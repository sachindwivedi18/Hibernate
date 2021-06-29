// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import rp.org.apache.http.entity.ContentType;
import rp.org.apache.http.client.utils.URLEncodedUtils;
import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.NameValuePair;
import java.util.List;
import rp.org.apache.http.entity.StringEntity;

public class UrlEncodedFormEntity extends StringEntity
{
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters, final String charset) throws UnsupportedEncodingException {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters, final Charset charset) {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this(parameters, (Charset)null);
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters) {
        this(parameters, null);
    }
}
