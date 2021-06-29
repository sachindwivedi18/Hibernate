// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.entity;

import rp.org.apache.http.HttpEntity;

public class DeflateDecompressingEntity extends DecompressingEntity
{
    public DeflateDecompressingEntity(final HttpEntity entity) {
        super(entity, DeflateInputStreamFactory.getInstance());
    }
}
