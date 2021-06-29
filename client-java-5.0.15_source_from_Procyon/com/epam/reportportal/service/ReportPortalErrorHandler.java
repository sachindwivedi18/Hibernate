// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import rp.org.apache.http.entity.ContentType;
import com.epam.ta.reportportal.ws.model.ErrorRS;
import java.io.IOException;
import com.epam.reportportal.exception.GeneralReportPortalException;
import java.nio.charset.StandardCharsets;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import com.epam.reportportal.exception.ReportPortalException;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import rp.com.google.common.io.ByteSource;
import com.epam.reportportal.restendpoint.http.Response;
import com.epam.reportportal.restendpoint.serializer.Serializer;
import org.slf4j.Logger;
import com.epam.reportportal.restendpoint.http.DefaultErrorHandler;

public class ReportPortalErrorHandler extends DefaultErrorHandler
{
    private static final Logger LOG;
    private final Serializer serializer;
    
    public ReportPortalErrorHandler(final Serializer serializer) {
        this.serializer = serializer;
    }
    
    @Override
    public void handle(final Response<ByteSource> rs) throws RestEndpointIOException {
        if (!this.hasError(rs)) {
            return;
        }
        this.handleError(rs);
    }
    
    @Override
    public boolean hasError(final Response<ByteSource> rs) {
        return super.hasError(rs) || this.isNotJson(rs);
    }
    
    private void handleError(final Response<ByteSource> rs) throws RestEndpointIOException {
        try {
			
			System.out.println("*****************************************");
			System.out.println("Response is : "+rs);
			System.out.println("*****************************************");			
            final ByteSource errorBody = rs.getBody();
            final int statusCode = rs.getStatus();
            final String statusMessage = rs.getReason();
            final byte[] body = errorBody.read();
            final ErrorRS errorRS = this.deserializeError(body);
            if (null != errorRS) {
                throw new ReportPortalException(statusCode, statusMessage, errorRS);
            }
            if (this.isNotJson(rs)) {
                throw new InternalReportPortalClientException(String.format("Report portal is not functioning correctly. Response is not json. Uri: [%s]; statusCode: [%s]; statusMessage: [%s];", rs.getUri(), statusCode, statusMessage));
            }
            throw new GeneralReportPortalException(statusCode, statusMessage, new String(body, StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new GeneralReportPortalException(rs.getStatus(), rs.getReason(), "Cannot read the response");
        }
    }
    
    private ErrorRS deserializeError(final byte[] content) {
        try {
            if (null != content) {
                return this.serializer.deserialize(content, ErrorRS.class);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private boolean isNotJson(final Response<ByteSource> rs) {
        boolean result = true;
        Collection<String> contentTypes = rs.getHeaders().get("Content-Type");
        if (contentTypes.isEmpty() && rs.getHeaders().containsKey("Content-Type".toLowerCase())) {
            contentTypes = rs.getHeaders().get("Content-Type".toLowerCase());
        }
        if (null != contentTypes) {
            for (final String contentType : contentTypes) {
                final boolean isJson = contentType.contains(ContentType.APPLICATION_JSON.getMimeType());
                if (isJson) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)ReportPortalErrorHandler.class);
    }
}
