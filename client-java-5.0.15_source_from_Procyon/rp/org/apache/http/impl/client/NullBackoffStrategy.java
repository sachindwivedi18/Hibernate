// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.ConnectionBackoffStrategy;

public class NullBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return false;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return false;
    }
}
