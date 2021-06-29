// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import rp.com.google.common.collect.ImmutableMultimap;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.collect.Multimap;
import java.net.URI;

public class Response<T>
{
    private final URI uri;
    private final HttpMethod httpMethod;
    private final int status;
    private final String reason;
    private final Multimap<String, String> headers;
    private final T body;
    
    public Response(final URI uri, final HttpMethod httpMethod, final int status, final String reason, final Multimap<String, String> headers, final T body) {
        Preconditions.checkArgument(null != uri, (Object)"URL shouldn't be null or empty");
        Preconditions.checkArgument(null != httpMethod, (Object)"HttpMethod shouldn't be null or empty");
        Preconditions.checkArgument(status > 0, "Incorrect status code: %s", status);
        Preconditions.checkArgument(null != headers, (Object)"Headers shouldn't be null");
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.status = status;
        this.reason = reason;
        this.headers = (Multimap<String, String>)ImmutableMultimap.copyOf((Multimap<?, ?>)headers);
        this.body = body;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public final int getStatus() {
        return this.status;
    }
    
    public final String getReason() {
        return this.reason;
    }
    
    public final Multimap<String, String> getHeaders() {
        return this.headers;
    }
    
    public final T getBody() {
        return this.body;
    }
    
    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }
}
