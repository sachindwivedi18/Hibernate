// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import rp.com.google.common.io.ByteSource;

public class NoopErrorHandler implements ErrorHandler
{
    @Override
    public boolean hasError(final Response<ByteSource> rs) {
        return false;
    }
    
    @Override
    public void handle(final Response<ByteSource> rs) throws RestEndpointIOException {
    }
}
