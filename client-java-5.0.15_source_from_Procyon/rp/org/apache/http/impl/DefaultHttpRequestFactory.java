// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl;

import rp.org.apache.http.MethodNotSupportedException;
import rp.org.apache.http.message.BasicHttpEntityEnclosingRequest;
import rp.org.apache.http.message.BasicHttpRequest;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.RequestLine;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.HttpRequestFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultHttpRequestFactory implements HttpRequestFactory
{
    public static final DefaultHttpRequestFactory INSTANCE;
    private static final String[] RFC2616_COMMON_METHODS;
    private static final String[] RFC2616_ENTITY_ENC_METHODS;
    private static final String[] RFC2616_SPECIAL_METHODS;
    private static final String[] RFC5789_ENTITY_ENC_METHODS;
    
    private static boolean isOneOf(final String[] methods, final String method) {
        for (final String method2 : methods) {
            if (method2.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public HttpRequest newHttpRequest(final RequestLine requestline) throws MethodNotSupportedException {
        Args.notNull(requestline, "Request line");
        final String method = requestline.getMethod();
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC5789_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    @Override
    public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException {
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC5789_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    static {
        INSTANCE = new DefaultHttpRequestFactory();
        RFC2616_COMMON_METHODS = new String[] { "GET" };
        RFC2616_ENTITY_ENC_METHODS = new String[] { "POST", "PUT" };
        RFC2616_SPECIAL_METHODS = new String[] { "HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT" };
        RFC5789_ENTITY_ENC_METHODS = new String[] { "PATCH" };
    }
}
