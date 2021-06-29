// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

public interface LaunchIdLock
{
    String obtainLaunchUuid(final String p0);
    
    void finishInstanceUuid(final String p0);
}
