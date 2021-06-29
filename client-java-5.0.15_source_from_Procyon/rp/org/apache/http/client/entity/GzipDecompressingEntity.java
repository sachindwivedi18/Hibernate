// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import rp.org.apache.http.HttpEntity;

public class GzipDecompressingEntity extends DecompressingEntity
{
    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity, GZIPInputStreamFactory.getInstance());
    }
}
