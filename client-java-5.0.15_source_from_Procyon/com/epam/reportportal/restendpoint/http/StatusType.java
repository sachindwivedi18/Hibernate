// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

public enum StatusType
{
    INFORMATIONAL(1), 
    SUCCESSFUL(2), 
    REDIRECTION(3), 
    CLIENT_ERROR(4), 
    SERVER_ERROR(5);
    
    private final int value;
    
    private StatusType(final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
    
    public static StatusType valueOf(final int status) {
        final int seriesCode = status / 100;
        for (final StatusType series : values()) {
            if (series.value == seriesCode) {
                return series;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + status + "]");
    }
}
