// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer.json;

import rp.com.google.common.net.MediaType;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.Closeable;
import com.epam.reportportal.restendpoint.http.IOUtils;
import java.io.IOException;
import java.io.Reader;
import rp.com.google.common.io.ByteSource;
import java.io.UnsupportedEncodingException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import rp.com.google.common.base.Charsets;
import com.google.gson.Gson;

public class GsonSerializer extends AbstractJsonSerializer
{
    private final Gson gson;
    
    public GsonSerializer(final Gson gson) {
        this.gson = gson;
    }
    
    public GsonSerializer() {
        this(new Gson());
    }
    
    @Override
    public <T> byte[] serialize(final T t) throws SerializerException {
        try {
            return this.gson.toJson((Object)t).getBytes(Charsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new SerializerException("UTF-8 is not supported", e);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        BufferedReader is = null;
        try {
            is = ByteSource.wrap(content).asCharSource(Charsets.UTF_8).openBufferedStream();
            return (T)this.gson.fromJson((Reader)is, (Class)clazz);
        }
        catch (IOException e) {
            throw new SerializerException("Unable to serialize content", e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        try {
            return (T)this.gson.getAdapter(TypeToken.get(type)).fromJson((Reader)ByteSource.wrap(content).asCharSource(Charsets.UTF_8).openBufferedStream());
        }
        catch (IOException e) {
            throw new SerializerException("Unable to serialize content", e);
        }
    }
}
