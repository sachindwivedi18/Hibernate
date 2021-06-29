// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer;

import rp.com.google.common.reflect.TypeToken;
import rp.com.google.common.net.MediaType;
import java.lang.reflect.Type;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;

public class ByteArraySerializer implements Serializer
{
    @Override
    public final <T> byte[] serialize(final T t) throws SerializerException {
        return (byte[])(Object)t;
    }
    
    @Override
    public final <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        if (byte[].class.equals(clazz)) {
            return (T)(Object)content;
        }
        throw new SerializerException("Unable to deserialize to type '" + clazz.getName() + "'");
    }
    
    @Override
    public final <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        if (byte[].class.equals(type)) {
            return (T)(Object)content;
        }
        throw new SerializerException("Unable to deserialize to type '" + type + "'");
    }
    
    @Override
    public final MediaType getMimeType() {
        return MediaType.OCTET_STREAM;
    }
    
    @Override
    public final boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        return mimeType.is(MediaType.ANY_TYPE) && byte[].class.equals(resultType);
    }
    
    @Override
    public final boolean canRead(final MediaType mimeType, final Type resultType) {
        return this.canRead(mimeType, TypeToken.of(resultType).getRawType());
    }
    
    @Override
    public final boolean canWrite(final Object o) {
        return byte[].class.equals(o.getClass());
    }
}
