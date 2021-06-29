// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.protocol.HttpContext;
import java.io.IOException;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.params.HttpParams;

public interface HttpClient
{
    @Deprecated
    HttpParams getParams();
    
    @Deprecated
    ClientConnectionManager getConnectionManager();
    
    HttpResponse execute(final HttpUriRequest p0) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpUriRequest p0, final HttpContext p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpHost p0, final HttpRequest p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(final HttpHost p0, final HttpRequest p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpUriRequest p0, final ResponseHandler<? extends T> p1) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpUriRequest p0, final ResponseHandler<? extends T> p1, final HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpHost p0, final HttpRequest p1, final ResponseHandler<? extends T> p2) throws IOException, ClientProtocolException;
    
     <T> T execute(final HttpHost p0, final HttpRequest p1, final ResponseHandler<? extends T> p2, final HttpContext p3) throws IOException, ClientProtocolException;
}
