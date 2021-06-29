// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.analytics;

import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import rp.org.apache.http.impl.client.CloseableHttpClient;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.client.entity.UrlEncodedFormEntity;
import java.nio.charset.StandardCharsets;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.methods.HttpPost;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.client.methods.HttpUriRequest;
import com.epam.reportportal.service.analytics.item.AnalyticsItem;
import rp.org.apache.http.impl.client.HttpClientBuilder;
import rp.org.apache.http.HttpHost;
import java.util.Optional;
import rp.org.apache.http.conn.HttpClientConnectionManager;
import rp.org.apache.http.impl.client.HttpClients;
import rp.org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import rp.org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import rp.org.apache.http.client.HttpClient;
import rp.org.apache.http.NameValuePair;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import java.io.Closeable;

public class GoogleAnalytics implements Closeable
{
    private static final Logger LOGGER;
    private static final Function<Map<String, String>, List<NameValuePair>> PARAMETERS_CONVERTER;
    private static final String DEFAULT_BASE_URL = "https://www.google-analytics.com/collect";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36";
    private final String baseUrl;
    private final List<NameValuePair> defaultRequestParams;
    private final HttpClient httpClient;
    
    public GoogleAnalytics(final String trackingId, final String proxyUrl) {
        this(trackingId, buildDefaultHttpClient(proxyUrl));
    }
    
    public GoogleAnalytics(final String trackingId, final HttpClient httpClient) {
        this.defaultRequestParams = new ArrayList<NameValuePair>();
        this.baseUrl = "https://www.google-analytics.com/collect";
        Collections.addAll(this.defaultRequestParams, new BasicNameValuePair[] { new BasicNameValuePair("de", "UTF-8"), new BasicNameValuePair("v", "1"), new BasicNameValuePair("cid", UUID.randomUUID().toString()), new BasicNameValuePair("tid", trackingId) });
        this.httpClient = httpClient;
    }
    
    private static HttpClient buildDefaultHttpClient(final String proxyUrl) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(1);
        final HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
        Optional.ofNullable(proxyUrl).ifPresent(u -> httpClientBuilder.setProxy(HttpHost.create(proxyUrl)));
        return httpClientBuilder.build();
    }
    
    public Boolean send(final AnalyticsItem item) {
        try {
            final HttpPost httpPost = this.buildPostRequest(item);
            final HttpResponse response = this.httpClient.execute(httpPost);
            try {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            finally {
                if (response instanceof CloseableHttpResponse) {
                    ((CloseableHttpResponse)response).close();
                }
            }
            return true;
        }
        catch (Throwable ex) {
            GoogleAnalytics.LOGGER.error(ex.getMessage());
            return false;
        }
    }
    
    private HttpPost buildPostRequest(final AnalyticsItem item) {
        final List<NameValuePair> nameValuePairs = GoogleAnalytics.PARAMETERS_CONVERTER.apply(item.getParams());
        nameValuePairs.addAll(this.defaultRequestParams);
        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
        final HttpPost httpPost = new HttpPost(this.baseUrl);
        httpPost.setEntity(entity);
        return httpPost;
    }
    
    @Override
    public void close() {
        if (this.httpClient instanceof CloseableHttpClient) {
            try {
                ((CloseableHttpClient)this.httpClient).close();
            }
            catch (Exception ex) {
                GoogleAnalytics.LOGGER.error(ex.getMessage());
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GoogleAnalytics.class);
        PARAMETERS_CONVERTER = (params -> (List)params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), (String)entry.getValue())).collect(Collectors.toList()));
    }
}
