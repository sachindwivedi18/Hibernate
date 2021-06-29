// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import java.lang.reflect.Proxy;
import rp.org.apache.http.client.methods.CloseableHttpResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.util.EntityUtils;
import rp.org.apache.http.HttpResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

@Deprecated
class CloseableHttpResponseProxy implements InvocationHandler
{
    private static final Constructor<?> CONSTRUCTOR;
    private final HttpResponse original;
    
    CloseableHttpResponseProxy(final HttpResponse original) {
        this.original = original;
    }
    
    public void close() throws IOException {
        final HttpEntity entity = this.original.getEntity();
        EntityUtils.consume(entity);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String mname = method.getName();
        if (mname.equals("close")) {
            this.close();
            return null;
        }
        try {
            return method.invoke(this.original, args);
        }
        catch (InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if (cause != null) {
                throw cause;
            }
            throw ex;
        }
    }
    
    public static CloseableHttpResponse newProxy(final HttpResponse original) {
        try {
            return (CloseableHttpResponse)CloseableHttpResponseProxy.CONSTRUCTOR.newInstance(new CloseableHttpResponseProxy(original));
        }
        catch (InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex2) {
            throw new IllegalStateException(ex2);
        }
        catch (IllegalAccessException ex3) {
            throw new IllegalStateException(ex3);
        }
    }
    
    static {
        try {
            CONSTRUCTOR = Proxy.getProxyClass(CloseableHttpResponseProxy.class.getClassLoader(), CloseableHttpResponse.class).getConstructor(InvocationHandler.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
