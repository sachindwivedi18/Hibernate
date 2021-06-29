// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer.json;

import rp.com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import rp.com.google.common.net.MediaType;
import com.epam.reportportal.restendpoint.serializer.Serializer;

abstract class AbstractJsonSerializer implements Serializer
{
    @Override
    public MediaType getMimeType() {
        return MediaType.JSON_UTF_8;
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        return MediaType.JSON_UTF_8.withoutParameters().is(mimeType.withoutParameters());
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Type resultType) {
        return this.canRead(mimeType, TypeToken.of(resultType).getRawType());
    }
    
    @Override
    public boolean canWrite(final Object o) {
        return true;
    }
}
