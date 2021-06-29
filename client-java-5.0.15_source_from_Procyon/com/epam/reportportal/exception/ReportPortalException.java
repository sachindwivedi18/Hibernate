// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.exception;

import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.base.Strings;
import com.epam.ta.reportportal.ws.model.ErrorRS;

public class ReportPortalException extends GeneralReportPortalException
{
    private static final int MAX_ERROR_MESSAGE_LENGTH = 100000;
    private static final long serialVersionUID = -3747137063782963453L;
    protected final ErrorRS error;
    
    public ReportPortalException(final int statusCode, final String statusMessage, final ErrorRS error) {
        super(statusCode, statusMessage, error.getMessage());
        this.error = error;
    }
    
    public ErrorRS getError() {
        return this.error;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Report Portal returned error\n").append("Status code: ").append(this.statusCode).append("\n").append("Status message: ").append(this.statusMessage).append("\n");
        if (null != this.error) {
            builder.append("Error Message: ").append(trimMessage(this.error.getMessage(), 100000)).append("\n").append("Error Type: ").append(this.error.getErrorType()).append("\n");
        }
        return builder.toString();
    }
    
    @VisibleForTesting
    static String trimMessage(final String message, final int maxLength) {
        if (Strings.isNullOrEmpty(message)) {
            return "";
        }
        if (message.length() > maxLength) {
            return message.substring(0, maxLength);
        }
        return message;
    }
}
