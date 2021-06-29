// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer;

import rp.com.google.common.net.MediaType;
import rp.com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import org.apache.commons.beanutils.ConversionException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import rp.com.google.common.base.Charsets;
import java.util.Iterator;
import org.apache.commons.beanutils.Converter;
import java.util.Map;
import org.apache.commons.beanutils.ConvertUtilsBean;

public class TextSerializer implements Serializer
{
    private final ConvertUtilsBean converter;
    
    public TextSerializer() {
        this(true, true, 0);
    }
    
    public TextSerializer(final boolean throwException, final boolean defaultNull, final int defaultArraySize) {
        (this.converter = new ConvertUtilsBean()).register(throwException, defaultNull, defaultArraySize);
        this.converter.deregister((Class)byte[].class);
        this.converter.deregister((Class)Byte[].class);
    }
    
    public TextSerializer(final Map<Converter, Class<?>> typeConverters) {
        this();
        for (final Map.Entry<Converter, Class<?>> typeConverter : typeConverters.entrySet()) {
            this.converter.register((Converter)typeConverter.getKey(), (Class)typeConverter.getValue());
        }
    }
    
    @Override
    public <T> byte[] serialize(final T t) throws SerializerException {
        try {
            return ((String)this.converter.lookup((Class)String.class).convert((Class)String.class, (Object)t)).getBytes(Charsets.UTF_8);
        }
        catch (ConversionException e) {
            throw new SerializerException("Cannot convert content '" + t + "' to string type", e.getCause());
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        final String stringContent = new String(content, Charsets.UTF_8);
        try {
            return (T)this.converter.lookup((Class)clazz).convert((Class)clazz, (Object)stringContent);
        }
        catch (ConversionException e) {
            throw new SerializerException("Cannot convert content '" + stringContent + "' to type [" + clazz + "]", e.getCause());
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        return this.deserialize(content, TypeToken.of(type).getRawType());
    }
    
    @Override
    public MediaType getMimeType() {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        return mimeType.withoutParameters().is(MediaType.ANY_TEXT_TYPE) && null != this.converter.lookup((Class)resultType, (Class)String.class);
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Type resultType) {
        return this.canRead(mimeType, TypeToken.of(resultType).getRawType());
    }
    
    @Override
    public boolean canWrite(final Object o) {
        return null != this.converter.lookup((Class)o.getClass());
    }
}
