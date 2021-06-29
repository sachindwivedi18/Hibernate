// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.exception;

public class RestEndpointIOException extends RuntimeException
{
    private static final long serialVersionUID = -5339772980222891685L;
    
    public RestEndpointIOException(final String message, final Throwable e) {
        super(message, e);
    }
    
    public RestEndpointIOException(final String message) {
        super(message);
    }
}
