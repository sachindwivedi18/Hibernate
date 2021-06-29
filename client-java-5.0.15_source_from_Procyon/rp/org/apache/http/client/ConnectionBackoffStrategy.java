// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.HttpResponse;

public interface ConnectionBackoffStrategy
{
    boolean shouldBackoff(final Throwable p0);
    
    boolean shouldBackoff(final HttpResponse p0);
}
