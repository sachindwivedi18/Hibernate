// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import rp.com.google.common.base.Preconditions;
import rp.com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;

public class RestCommand<RQ, RS>
{
    private final HttpMethod httpMethod;
    private final RQ request;
    private final String uri;
    private final Type responseType;
    private final boolean multipart;
    
    public RestCommand(final String uri, final HttpMethod method, final RQ request, final Class<RS> responseClass) {
        this(uri, method, request, TypeToken.of(responseClass).getType(), false);
    }
    
    public RestCommand(final String uri, final HttpMethod method, final RQ request, final Class<RS> responseClass, final boolean multipart) {
        this(uri, method, request, TypeToken.of(responseClass).getType(), multipart);
    }
    
    public RestCommand(final String uri, final HttpMethod method, final RQ request, final Type responseType, final boolean multipart) {
        this.httpMethod = method;
        this.request = request;
        this.uri = uri;
        this.responseType = responseType;
        this.multipart = multipart;
        this.validate();
    }
    
    public final HttpMethod getHttpMethod() {
        return this.httpMethod;
    }
    
    public final boolean isMultipart() {
        return this.multipart;
    }
    
    public final RQ getRequest() {
        return this.request;
    }
    
    public final String getUri() {
        return this.uri;
    }
    
    public final Type getResponseType() {
        return this.responseType;
    }
    
    private void validate() {
        if (!this.httpMethod.hasBody()) {
            Preconditions.checkState(null == this.request, "'%s' shouldn't contain body", this.httpMethod);
            Preconditions.checkState(!this.multipart, "Incorrect request type for multipart: '%s'", this.httpMethod);
        }
    }
}
