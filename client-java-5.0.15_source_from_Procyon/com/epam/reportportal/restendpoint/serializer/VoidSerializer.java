// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer;

import rp.com.google.common.reflect.TypeToken;
import rp.com.google.common.net.MediaType;
import java.lang.reflect.Type;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;

public class VoidSerializer implements Serializer
{
    @Override
    public final <T> byte[] serialize(final T t) throws SerializerException {
        throw new UnsupportedOperationException("Serialization is not permitted for Void types");
    }
    
    @Override
    public final <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        return null;
    }
    
    @Override
    public final <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        return null;
    }
    
    @Override
    public final MediaType getMimeType() {
        throw new UnsupportedOperationException("Void type doesn't have mime type");
    }
    
    @Override
    public final boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        return Void.class.equals(resultType);
    }
    
    @Override
    public final boolean canRead(final MediaType mimeType, final Type resultType) {
        final TypeToken type = TypeToken.of(resultType);
        return Void.TYPE.equals(type.getType()) || this.canRead(mimeType, type.getRawType());
    }
    
    @Override
    public final boolean canWrite(final Object o) {
        return false;
    }
}
