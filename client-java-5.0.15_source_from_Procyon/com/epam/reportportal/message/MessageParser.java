// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.message;

import java.io.IOException;

public interface MessageParser
{
    public static final String RP_MESSAGE_PREFIX = "RP_MESSAGE";
    
    ReportPortalMessage parse(final String p0) throws IOException;
    
    boolean supports(final String p0);
}
