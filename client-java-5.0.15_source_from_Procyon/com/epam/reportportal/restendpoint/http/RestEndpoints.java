// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;
import rp.org.apache.http.conn.ssl.DefaultHostnameVerifier;
import rp.org.apache.http.ssl.TrustStrategy;
import rp.org.apache.http.ssl.SSLContexts;
import java.io.InputStream;
import rp.org.apache.http.client.CredentialsProvider;
import rp.org.apache.http.HttpRequestInterceptor;
import rp.org.apache.http.auth.Credentials;
import rp.org.apache.http.auth.UsernamePasswordCredentials;
import rp.org.apache.http.auth.AuthScope;
import rp.org.apache.http.impl.client.BasicCredentialsProvider;
import rp.org.apache.http.impl.client.HttpClientBuilder;
import java.lang.reflect.InvocationHandler;
import rp.com.google.common.reflect.Reflection;
import com.epam.reportportal.restendpoint.http.proxy.RestEndpointInvocationHandler;
import java.util.List;
import rp.org.apache.http.client.HttpClient;
import rp.com.google.common.collect.Lists;
import com.epam.reportportal.restendpoint.serializer.json.GsonSerializer;
import com.epam.reportportal.restendpoint.serializer.ByteArraySerializer;
import com.epam.reportportal.restendpoint.serializer.TextSerializer;
import com.epam.reportportal.restendpoint.serializer.Serializer;
import rp.org.apache.http.impl.client.HttpClients;

public final class RestEndpoints
{
    private RestEndpoints() {
    }
    
    public static RestEndpoint createDefault() {
        return new HttpClientRestEndpoint(HttpClients.createDefault(), Lists.newArrayList(new TextSerializer(), new ByteArraySerializer(), new GsonSerializer()), new DefaultErrorHandler());
    }
    
    public static RestEndpoint createDefault(final String endpointUrl) {
        return new HttpClientRestEndpoint(HttpClients.createDefault(), Lists.newArrayList(new TextSerializer(), new ByteArraySerializer(), new GsonSerializer()), new DefaultErrorHandler(), endpointUrl);
    }
    
    public static <T> T forInterface(final Class<T> clazz, final RestEndpoint endpoint) {
        return Reflection.newProxy(clazz, new RestEndpointInvocationHandler(clazz, endpoint));
    }
    
    public static Builder create() {
        return new Builder();
    }
    
    public static class Builder
    {
        private final List<Serializer> serializers;
        private final HttpClientBuilder httpClientBuilder;
        private HttpClient httpClient;
        private ErrorHandler errorHandler;
        private String endpointUrl;
        
        Builder() {
            this.serializers = (List<Serializer>)Lists.newArrayList();
            this.httpClientBuilder = HttpClientBuilder.create();
        }
        
        public final RestEndpoint build() {
            HttpClient closeableHttpClient;
            if (null == this.httpClient) {
                closeableHttpClient = this.httpClientBuilder.build();
            }
            else {
                closeableHttpClient = this.httpClient;
            }
            return new HttpClientRestEndpoint(closeableHttpClient, this.serializers, this.errorHandler, this.endpointUrl);
        }
        
        public final Builder withBaseUrl(final String url) {
            this.endpointUrl = url;
            return this;
        }
        
        public final Builder withErrorHandler(final ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }
        
        public final Builder withSerializer(final Serializer serializer) {
            this.serializers.add(serializer);
            return this;
        }
        
        public final Builder withHttpClient(final HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }
        
        public final Builder withBasicAuth(final String username, final String password) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            this.httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            this.httpClientBuilder.addInterceptorFirst(new PreemptiveAuthInterceptor());
            return this;
        }
        
        public final Builder withSsl(final InputStream keyStore, final String keyStorePass) {
            SSLContext sslcontext;
            try {
                sslcontext = SSLContexts.custom().loadTrustMaterial(IOUtils.loadKeyStore(keyStore, keyStorePass), null).build();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Unable to load trust store", e);
            }
            this.httpClientBuilder.setSSLContext(sslcontext).setSSLHostnameVerifier(new DefaultHostnameVerifier());
            return this;
        }
        
        public final <T> T forInterface(final Class<T> clazz) {
            return RestEndpoints.forInterface(clazz, this.build());
        }
    }
}
