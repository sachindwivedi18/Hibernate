// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.methods;

import rp.org.apache.http.concurrent.Cancellable;

public interface HttpExecutionAware
{
    boolean isAborted();
    
    void setCancellable(final Cancellable p0);
}
