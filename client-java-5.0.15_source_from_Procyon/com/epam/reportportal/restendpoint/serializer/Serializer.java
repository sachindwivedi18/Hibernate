// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer;

import rp.com.google.common.net.MediaType;
import java.lang.reflect.Type;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;

public interface Serializer
{
     <T> byte[] serialize(final T p0) throws SerializerException;
    
     <T> T deserialize(final byte[] p0, final Class<T> p1) throws SerializerException;
    
     <T> T deserialize(final byte[] p0, final Type p1) throws SerializerException;
    
    MediaType getMimeType();
    
    boolean canRead(final MediaType p0, final Class<?> p1);
    
    boolean canRead(final MediaType p0, final Type p1);
    
    boolean canWrite(final Object p0);
}
