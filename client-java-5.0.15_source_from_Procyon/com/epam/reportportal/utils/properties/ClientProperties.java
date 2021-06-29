// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.properties;

public enum ClientProperties implements PropertyHolder
{
    CLIENT("client", false, new String[] { "client.name", "client.version" });
    
    private final String name;
    private final boolean internal;
    private final String[] propertyKeys;
    
    private ClientProperties(final String name, final boolean internal, final String[] propertyKeys) {
        this.name = name;
        this.internal = internal;
        this.propertyKeys = propertyKeys;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String[] getPropertyKeys() {
        return this.propertyKeys;
    }
    
    @Override
    public boolean isInternal() {
        return this.internal;
    }
}
