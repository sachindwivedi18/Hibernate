// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.HttpResponse;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import rp.org.apache.http.client.ConnectionBackoffStrategy;

public class DefaultBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return t instanceof SocketTimeoutException || t instanceof ConnectException;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == 503;
    }
}
