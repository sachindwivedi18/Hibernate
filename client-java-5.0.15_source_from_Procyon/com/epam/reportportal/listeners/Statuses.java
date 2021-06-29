// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.listeners;

@Deprecated
public final class Statuses
{
    public static final String PASSED;
    public static final String FAILED;
    public static final String SKIPPED;
    
    private Statuses() {
    }
    
    static {
        PASSED = ItemStatus.PASSED.name();
        FAILED = ItemStatus.FAILED.name();
        SKIPPED = ItemStatus.SKIPPED.name();
    }
}
