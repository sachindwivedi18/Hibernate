// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.serializer.xml;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;
import rp.com.google.common.net.MediaType;
import rp.com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import com.epam.reportportal.restendpoint.http.IOUtils;
import javax.xml.transform.Result;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import javax.xml.bind.JAXBContext;
import com.epam.reportportal.restendpoint.serializer.Serializer;

public class JaxbSerializer implements Serializer
{
    private final JAXBContext jaxbContext;
    
    public JaxbSerializer(final JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }
    
    public JaxbSerializer(final Class... classes) throws SerializerException {
        try {
            this.jaxbContext = JAXBContext.newInstance(classes);
        }
        catch (JAXBException e) {
            throw new SerializerException("Unable to create JaxbContext", (Throwable)e);
        }
    }
    
    public JaxbSerializer(final String contextPath) throws SerializerException {
        try {
            this.jaxbContext = JAXBContext.newInstance(contextPath);
        }
        catch (JAXBException e) {
            throw new SerializerException("Unable to create JaxbContext", (Throwable)e);
        }
    }
    
    @Override
    public <T> byte[] serialize(final T t) throws SerializerException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final StreamResult result = new StreamResult(baos);
            this.jaxbContext.createMarshaller().marshal((Object)t, (Result)result);
            return baos.toByteArray();
        }
        catch (JAXBException e) {
            throw new SerializerException("Unable to serialize xml", (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly(baos);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Class<T> clazz) throws SerializerException {
        try {
            final InputStream is = new ByteArrayInputStream(content);
            final JAXBElement<T> result = (JAXBElement<T>)this.jaxbContext.createUnmarshaller().unmarshal((Source)new StreamSource(is), (Class)clazz);
            return (T)result.getValue();
        }
        catch (JAXBException e) {
            throw new SerializerException("Unable to deserialize xml", (Throwable)e);
        }
    }
    
    @Override
    public <T> T deserialize(final byte[] content, final Type type) throws SerializerException {
        return this.deserialize(content, TypeToken.of(type).getRawType());
    }
    
    @Override
    public MediaType getMimeType() {
        return MediaType.APPLICATION_XML_UTF_8;
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Class<?> resultType) {
        return this.canRead(mimeType, TypeToken.of(resultType).getType());
    }
    
    @Override
    public boolean canRead(final MediaType mimeType, final Type resultType) {
        return mimeType.withoutParameters().is(MediaType.APPLICATION_XML_UTF_8.withoutParameters());
    }
    
    @Override
    public boolean canWrite(final Object o) {
        return null != o && o.getClass().isAnnotationPresent((Class<? extends Annotation>)XmlRootElement.class);
    }
}
