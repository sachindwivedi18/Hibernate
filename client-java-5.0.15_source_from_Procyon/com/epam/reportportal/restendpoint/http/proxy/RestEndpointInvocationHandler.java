// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.proxy;

import com.epam.reportportal.restendpoint.http.Response;
import io.reactivex.Maybe;
import io.reactivex.functions.Function;
import rp.com.google.common.base.Preconditions;
import com.epam.reportportal.restendpoint.http.annotation.Close;
import com.epam.reportportal.restendpoint.http.RestEndpoint;
import java.lang.reflect.Method;
import java.util.Map;
import com.epam.reportportal.restendpoint.http.HttpClientRestEndpoint;
import java.lang.reflect.InvocationHandler;

public class RestEndpointInvocationHandler implements InvocationHandler
{
    public static final HttpClientRestEndpoint.BodyTransformer<Object> BODY_TRANSFORMER;
    private final Map<Method, RestMethodInfo> restMethods;
    private final RestEndpoint delegate;
    private String closeMethod;
    
    public RestEndpointInvocationHandler(final Class<?> clazz, final RestEndpoint restEndpoint) {
        this.delegate = restEndpoint;
        this.restMethods = RestMethodInfo.mapMethods(clazz);
        for (final Method m : clazz.getMethods()) {
            if (null != m.getAnnotation(Close.class)) {
                this.closeMethod = m.getName();
            }
        }
    }
    
    @Override
    public final Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.executeRestMethod(method, args);
    }
    
    private Object executeRestMethod(final Method method, final Object[] args) throws Throwable {
        if (null != this.closeMethod && this.closeMethod.equals(method.getName())) {
            this.delegate.close();
            return null;
        }
        Preconditions.checkState(this.restMethods.containsKey(method), "Method with name [%s] is not mapped", method.getName());
        final RestMethodInfo methodInfo = this.restMethods.get(method);
        final Maybe<Response<Object>> response = this.delegate.executeRequest(methodInfo.createRestCommand(args));
        final Maybe<?> result = (Maybe<?>)(methodInfo.isBodyOnly() ? response.flatMap((Function)RestEndpointInvocationHandler.BODY_TRANSFORMER) : response);
        if (methodInfo.isAsynchronous()) {
            return result;
        }
        return result.blockingGet();
    }
    
    static {
        BODY_TRANSFORMER = new HttpClientRestEndpoint.BodyTransformer<Object>();
    }
}
