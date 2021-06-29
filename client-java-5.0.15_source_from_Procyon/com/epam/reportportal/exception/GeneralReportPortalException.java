// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.exception;

public class GeneralReportPortalException extends RuntimeException
{
    private static final long serialVersionUID = -3747137063782963453L;
    protected final int statusCode;
    protected final String statusMessage;
    
    public GeneralReportPortalException(final int statusCode, final String statusMessage, final String errorContent) {
        super(errorContent);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Report Portal returned error\n").append("Status code: ").append(this.statusCode).append("\n").append("Status message: ").append(this.statusMessage).append("\n");
        if (null != super.getMessage()) {
            builder.append("Error Message: ").append(super.getMessage()).append("\n");
        }
        return builder.toString();
    }
}
