// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.exception;

public class InternalReportPortalClientException extends RuntimeException
{
    private static final long serialVersionUID = -4231070395029601011L;
    
    public InternalReportPortalClientException(final String message, final Exception e) {
        super(message, e);
    }
    
    public InternalReportPortalClientException(final String message) {
        super(message);
    }
}
