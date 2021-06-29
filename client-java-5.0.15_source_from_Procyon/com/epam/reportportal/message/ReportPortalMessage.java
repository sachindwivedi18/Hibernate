// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.message;

import java.io.IOException;
import com.epam.reportportal.utils.files.Utils;
import java.io.File;
import rp.com.google.common.io.ByteSource;

public class ReportPortalMessage
{
    private TypeAwareByteSource data;
    private String message;
    
    public ReportPortalMessage() {
    }
    
    public ReportPortalMessage(final String message) {
        this.message = message;
    }
    
    public ReportPortalMessage(final ByteSource data, final String mediaType, final String message) {
        this(message);
        this.data = new TypeAwareByteSource(data, mediaType);
    }
    
    public ReportPortalMessage(final TypeAwareByteSource data, final String message) {
        this(message);
        this.data = data;
    }
    
    public ReportPortalMessage(final File file, final String message) throws IOException {
        this(Utils.getFile(file), message);
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public TypeAwareByteSource getData() {
        return this.data;
    }
}
