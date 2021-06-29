// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.client.protocol.RequestProxyAuthentication;
import rp.org.apache.http.client.protocol.RequestTargetAuthentication;
import rp.org.apache.http.client.protocol.RequestAuthCache;
import rp.org.apache.http.HttpResponseInterceptor;
import rp.org.apache.http.client.protocol.ResponseProcessCookies;
import rp.org.apache.http.client.protocol.RequestAddCookies;
import rp.org.apache.http.protocol.RequestExpectContinue;
import rp.org.apache.http.protocol.RequestUserAgent;
import rp.org.apache.http.client.protocol.RequestClientConnControl;
import rp.org.apache.http.protocol.RequestTargetHost;
import rp.org.apache.http.protocol.RequestContent;
import rp.org.apache.http.HttpRequestInterceptor;
import rp.org.apache.http.client.protocol.RequestDefaultHeaders;
import rp.org.apache.http.protocol.BasicHttpProcessor;
import rp.org.apache.http.util.VersionInfo;
import rp.org.apache.http.params.HttpConnectionParams;
import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.params.HttpProtocolParams;
import rp.org.apache.http.HttpVersion;
import rp.org.apache.http.params.SyncBasicHttpParams;
import rp.org.apache.http.params.HttpParams;
import rp.org.apache.http.conn.ClientConnectionManager;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class DefaultHttpClient extends AbstractHttpClient
{
    public DefaultHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
    }
    
    public DefaultHttpClient(final ClientConnectionManager conman) {
        super(conman, null);
    }
    
    public DefaultHttpClient(final HttpParams params) {
        super(null, params);
    }
    
    public DefaultHttpClient() {
        super(null, null);
    }
    
    @Override
    protected HttpParams createHttpParams() {
        final HttpParams params = new SyncBasicHttpParams();
        setDefaultHttpParams(params);
        return params;
    }
    
    public static void setDefaultHttpParams(final HttpParams params) {
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEF_CONTENT_CHARSET.name());
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, VersionInfo.getUserAgent("Apache-HttpClient", "rp.org.apache.http.client", DefaultHttpClient.class));
    }
    
    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestDefaultHeaders());
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        httpproc.addInterceptor(new RequestClientConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        httpproc.addInterceptor(new RequestAddCookies());
        httpproc.addInterceptor(new ResponseProcessCookies());
        httpproc.addInterceptor(new RequestAuthCache());
        httpproc.addInterceptor(new RequestTargetAuthentication());
        httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }
}
