// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.client.HttpResponseException;
import rp.org.apache.http.HttpResponse;
import java.io.IOException;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicResponseHandler extends AbstractResponseHandler<String>
{
    @Override
    public String handleEntity(final HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }
    
    @Override
    public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        return super.handleResponse(response);
    }
}
