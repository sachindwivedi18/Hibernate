// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import rp.com.google.common.base.Preconditions;
import java.util.ArrayList;
import rp.com.google.common.io.ByteSource;
import java.util.List;

public class MultiPartRequest
{
    private final List<MultiPartSerialized<?>> serializedRQs;
    private final List<MultiPartBinary> binaryRQs;
    
    public MultiPartRequest(final List<MultiPartSerialized<?>> serializedRQs, final List<MultiPartBinary> binaryRQs) {
        this.serializedRQs = serializedRQs;
        this.binaryRQs = binaryRQs;
    }
    
    public final List<MultiPartBinary> getBinaryRQs() {
        return this.binaryRQs;
    }
    
    public final List<MultiPartSerialized<?>> getSerializedRQs() {
        return this.serializedRQs;
    }
    
    public static class MultiPartSerialized<RQ>
    {
        private final String partName;
        private final RQ request;
        
        public MultiPartSerialized(final String partName, final RQ request) {
            this.partName = partName;
            this.request = request;
        }
        
        public final String getPartName() {
            return this.partName;
        }
        
        public final RQ getRequest() {
            return this.request;
        }
    }
    
    public static class MultiPartBinary
    {
        private final String partName;
        private final String filename;
        private final String contentType;
        private final ByteSource data;
        
        public MultiPartBinary(final String partName, final String filename, final String contentType, final ByteSource data) {
            this.partName = partName;
            this.filename = filename;
            this.data = data;
            this.contentType = contentType;
        }
        
        public final ByteSource getData() {
            return this.data;
        }
        
        public final String getFilename() {
            return this.filename;
        }
        
        public final String getPartName() {
            return this.partName;
        }
        
        public final String getContentType() {
            return this.contentType;
        }
    }
    
    public static class Builder
    {
        private final List<MultiPartSerialized<?>> serializedRQs;
        private final List<MultiPartBinary> binaryRQs;
        
        public Builder() {
            this.serializedRQs = new ArrayList<MultiPartSerialized<?>>();
            this.binaryRQs = new ArrayList<MultiPartBinary>();
        }
        
        public <RQ> Builder addSerializedPart(final String partName, final RQ body) {
            this.serializedRQs.add(new MultiPartSerialized<Object>(partName, body));
            return this;
        }
        
        public Builder addBinaryPart(final String partName, final String filename, final String contentType, final ByteSource data) {
            Preconditions.checkNotNull(data, (Object)"Provided data shouldn't be null");
            this.binaryRQs.add(new MultiPartBinary(partName, filename, contentType, data));
            return this;
        }
        
        public MultiPartRequest build() {
            return new MultiPartRequest(this.serializedRQs, this.binaryRQs);
        }
    }
}
