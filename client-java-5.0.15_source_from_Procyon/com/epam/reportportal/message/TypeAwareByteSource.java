// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import rp.com.google.common.io.ByteSource;

public class TypeAwareByteSource extends ByteSource
{
    private final ByteSource delegate;
    private final String mediaType;
    
    public TypeAwareByteSource(final ByteSource delegate, final String mediaType) {
        this.delegate = Objects.requireNonNull(delegate);
        this.mediaType = mediaType;
    }
    
    @Override
    public InputStream openStream() throws IOException {
        return this.delegate.openStream();
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
}
