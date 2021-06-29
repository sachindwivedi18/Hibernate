// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import rp.com.google.common.io.ByteSource;

public interface ErrorHandler
{
    boolean hasError(final Response<ByteSource> p0);
    
    void handle(final Response<ByteSource> p0) throws RestEndpointIOException;
}
