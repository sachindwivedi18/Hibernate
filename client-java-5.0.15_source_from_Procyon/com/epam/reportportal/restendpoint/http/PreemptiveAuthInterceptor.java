// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import java.io.IOException;
import rp.org.apache.http.HttpException;
import rp.org.apache.http.client.AuthCache;
import rp.org.apache.http.auth.AuthScheme;
import rp.org.apache.http.impl.auth.BasicScheme;
import rp.org.apache.http.impl.client.BasicAuthCache;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.auth.AuthState;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpRequestInterceptor;

public class PreemptiveAuthInterceptor implements HttpRequestInterceptor
{
    @Override
    public final void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        final AuthState authState = (AuthState)context.getAttribute("http.auth.target-scope");
        if (authState.getAuthScheme() == null) {
            final HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
            final AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());
            context.setAttribute("http.auth.auth-cache", authCache);
        }
    }
}
