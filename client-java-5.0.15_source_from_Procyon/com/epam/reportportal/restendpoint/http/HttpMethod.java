// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

public enum HttpMethod
{
    GET(false), 
    POST(true), 
    PUT(true), 
    PATCH(true), 
    DELETE(false);
    
    private final boolean hasBody;
    
    private HttpMethod(final boolean hasBody) {
        this.hasBody = hasBody;
    }
    
    boolean hasBody() {
        return this.hasBody;
    }
}
