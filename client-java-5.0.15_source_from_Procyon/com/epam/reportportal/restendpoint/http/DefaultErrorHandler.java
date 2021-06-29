// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.http.exception.RestEndpointException;
import java.net.URI;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import rp.com.google.common.io.ByteSource;

public class DefaultErrorHandler implements ErrorHandler
{
    @Override
    public boolean hasError(final Response<ByteSource> rs) {
        final StatusType statusType = StatusType.valueOf(rs.getStatus());
        return statusType == StatusType.CLIENT_ERROR || statusType == StatusType.SERVER_ERROR;
    }
    
    @Override
    public void handle(final Response<ByteSource> rs) throws RestEndpointIOException {
        if (!this.hasError(rs)) {
            return;
        }
        this.handleError(rs.getUri(), rs.getHttpMethod(), rs.getStatus(), rs.getReason(), rs.getBody());
    }
    
    protected void handleError(final URI requestUri, final HttpMethod requestMethod, final int statusCode, final String statusMessage, final ByteSource errorBody) throws RestEndpointIOException {
        throw new RestEndpointException(requestUri, requestMethod, statusCode, statusMessage, errorBody);
    }
}
