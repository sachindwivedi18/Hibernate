// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer.json;

import rp.com.google.common.net.MediaType;
import java.lang.reflect.Type;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer extends AbstractJsonSerializer
{
    private final ObjectMapper objectMapper;
    
    public JacksonSerializer(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public JacksonSerializer() {
        this(new ObjectMapper());
    }
    
    @Override
    public <T> byte[] serialize(final T t) throws SerializerException {
        try {
            return this.objectMapper.writeValueAsBytes((Object)t);
        }
        catch (JsonProcessingException e) {
            throw new SerializerException("Unable to serialize content", (Throwable)e);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        try {
            return (T)this.objectMapper.readValue(content, (Class)clazz);
        }
        catch (IOException e) {
            throw new SerializerException("Unable to deserialize content", e);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        try {
            return (T)this.objectMapper.readValue(content, this.objectMapper.getTypeFactory().constructType(type));
        }
        catch (IOException e) {
            throw new SerializerException("Unable to deserialize content", e);
        }
    }
}
