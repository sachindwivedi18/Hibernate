// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.io.IOException;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.StatusLine;
import rp.org.apache.http.client.HttpResponseException;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.ResponseHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractResponseHandler<T> implements ResponseHandler<T>
{
    @Override
    public T handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return (entity == null) ? null : this.handleEntity(entity);
    }
    
    public abstract T handleEntity(final HttpEntity p0) throws IOException;
}
