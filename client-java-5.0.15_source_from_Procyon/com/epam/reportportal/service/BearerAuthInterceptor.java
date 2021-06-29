// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpRequestInterceptor;

public class BearerAuthInterceptor implements HttpRequestInterceptor
{
    private final String uuid;
    
    public BearerAuthInterceptor(final String uuid) {
        this.uuid = uuid;
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        request.setHeader("Authorization", "bearer " + this.uuid);
    }
}
