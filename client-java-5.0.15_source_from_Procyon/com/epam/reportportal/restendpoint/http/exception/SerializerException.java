// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.exception;

public class SerializerException extends RestEndpointIOException
{
    private static final long serialVersionUID = 1L;
    
    public SerializerException(final String message) {
        super(message);
    }
    
    public SerializerException(final String message, final Throwable e) {
        super(message, e);
    }
}
