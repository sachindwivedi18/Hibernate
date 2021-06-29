// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.conn.HttpClientConnectionManager;
import rp.org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClients
{
    private HttpClients() {
    }
    
    public static HttpClientBuilder custom() {
        return HttpClientBuilder.create();
    }
    
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }
    
    public static CloseableHttpClient createSystem() {
        return HttpClientBuilder.create().useSystemProperties().build();
    }
    
    public static CloseableHttpClient createMinimal() {
        return new MinimalHttpClient(new PoolingHttpClientConnectionManager());
    }
    
    public static CloseableHttpClient createMinimal(final HttpClientConnectionManager connManager) {
        return new MinimalHttpClient(connManager);
    }
}
