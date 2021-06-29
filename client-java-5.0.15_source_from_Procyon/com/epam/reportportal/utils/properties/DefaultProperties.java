// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.properties;

public enum DefaultProperties implements PropertyHolder
{
    OS("os", true, new String[] { "os.name", "os.arch", "os.version" }), 
    JVM("jvm", true, new String[] { "java.vm.name", "java.version", "java.class.version" }), 
    AGENT("agent", false, new String[] { "agent.name", "agent.version" });
    
    private final String name;
    private final boolean internal;
    private final String[] propertyKeys;
    
    private DefaultProperties(final String name, final boolean internal, final String[] propertyKeys) {
        this.name = name;
        this.internal = internal;
        this.propertyKeys = propertyKeys;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isInternal() {
        return this.internal;
    }
    
    @Override
    public String[] getPropertyKeys() {
        return this.propertyKeys;
    }
}
