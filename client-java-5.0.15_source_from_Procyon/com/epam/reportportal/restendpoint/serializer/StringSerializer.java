// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer;

import rp.com.google.common.reflect.TypeToken;
import rp.com.google.common.net.MediaType;
import java.lang.reflect.Type;
import rp.com.google.common.base.Charsets;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;

@Deprecated
public class StringSerializer implements Serializer
{
    @Override
    public <T> byte[] serialize(final T t) throws SerializerException {
        return t.toString().getBytes();
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        this.validateString(clazz);
        return (T)new String(content, Charsets.UTF_8);
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        this.validateString(type);
        return (T)new String(content, Charsets.UTF_8);
    }
    
    @Override
    public MediaType getMimeType() {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        final MediaType type = mimeType.withoutParameters();
        return (type.is(MediaType.ANY_TEXT_TYPE) || MediaType.APPLICATION_XML_UTF_8.withoutParameters().is(type) || MediaType.JSON_UTF_8.withoutParameters().is(type)) && String.class.equals(resultType);
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Type resultType) {
        final MediaType type = mimeType.withoutParameters();
        return (type.is(MediaType.ANY_TEXT_TYPE) || MediaType.APPLICATION_XML_UTF_8.withoutParameters().is(type) || MediaType.JSON_UTF_8.withoutParameters().is(type)) && String.class.equals(TypeToken.of(resultType).getRawType());
    }
    
    @Override
    public boolean canWrite(final Object o) {
        return String.class.isAssignableFrom(o.getClass());
    }
    
    private void validateString(final Class<?> clazz) throws SerializerException {
        if (null != clazz && !clazz.isAssignableFrom(String.class)) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }
    
    private void validateString(final Type type) throws SerializerException {
        if (null == type || !String.class.equals(TypeToken.of(type).getRawType())) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }
}
