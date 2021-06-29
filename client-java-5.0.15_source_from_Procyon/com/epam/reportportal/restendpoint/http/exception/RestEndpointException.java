// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.exception;

import rp.com.google.common.io.ByteSource;
import com.epam.reportportal.restendpoint.http.HttpMethod;
import java.net.URI;

public class RestEndpointException extends RuntimeException
{
    private static final long serialVersionUID = 728718628763519460L;
    private final URI requestUri;
    private final HttpMethod requestMethod;
    private final int statusCode;
    private final String statusMessage;
    private final ByteSource content;
    
    public RestEndpointException(final URI requestUri, final HttpMethod requestMethod, final int statusCode, final String statusMessage, final ByteSource content) {
        this.requestUri = requestUri;
        this.requestMethod = requestMethod;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.content = content;
    }
    
    public URI getRequestUri() {
        return this.requestUri;
    }
    
    public HttpMethod getRequestMethod() {
        return this.requestMethod;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    public ByteSource getContent() {
        return this.content;
    }
    
    @Override
    public String getMessage() {
        return "Request [" + this.requestMethod.toString() + "] " + "to URL: " + this.requestUri + " has failed with " + "Status code: " + this.statusCode + '\n' + "Status message: " + this.statusMessage + '\n' + "Content: '" + this.content + '\'';
    }
}
