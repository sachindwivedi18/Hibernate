// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.proxy;

import java.lang.reflect.ParameterizedType;
import com.epam.reportportal.restendpoint.http.RestCommand;
import java.util.Iterator;
import rp.com.google.common.base.Joiner;
import java.util.Set;
import rp.com.google.common.collect.Sets;
import rp.com.google.common.collect.ImmutableList;
import com.epam.reportportal.restendpoint.http.annotation.Query;
import rp.com.google.common.reflect.TypeToken;
import com.epam.reportportal.restendpoint.http.MultiPartRequest;
import com.epam.reportportal.restendpoint.http.annotation.Multipart;
import com.epam.reportportal.restendpoint.http.annotation.Body;
import rp.com.google.common.base.Preconditions;
import com.epam.reportportal.restendpoint.http.annotation.Path;
import rp.com.google.common.reflect.Parameter;
import java.util.LinkedHashMap;
import com.epam.reportportal.restendpoint.http.Response;
import io.reactivex.Maybe;
import rp.com.google.common.reflect.Invokable;
import java.lang.annotation.Annotation;
import com.epam.reportportal.restendpoint.http.annotation.Request;
import java.util.HashMap;
import java.lang.reflect.Method;
import rp.com.google.common.base.Optional;
import com.epam.reportportal.restendpoint.http.uri.UrlTemplate;
import java.lang.reflect.Type;
import com.epam.reportportal.restendpoint.http.HttpMethod;
import java.util.Map;

class RestMethodInfo
{
    private final Map<Integer, String> pathArguments;
    private HttpMethod method;
    private Type responseType;
    private boolean asynchronous;
    private UrlTemplate urlTemplate;
    private Optional<Integer> bodyArgument;
    private Optional<Integer> queryParameter;
    private boolean returnBodyOnly;
    private boolean multiPart;
    
    public static Map<Method, RestMethodInfo> mapMethods(final Class<?> clazz) {
        final Map<Method, RestMethodInfo> methodInfo = new HashMap<Method, RestMethodInfo>();
        for (final Method method : clazz.getMethods()) {
            if (isRestMethodDefinition(method)) {
                methodInfo.put(method, new RestMethodInfo(method));
            }
        }
        return methodInfo;
    }
    
    static boolean isRestMethodDefinition(final Method m) {
        return m.isAnnotationPresent(Request.class);
    }
    
    static boolean isAsynchronous(final Invokable<?, ?> method) {
        return Maybe.class.isAssignableFrom(method.getReturnType().getRawType());
    }
    
    static boolean bodyOnly(final Invokable<?, ?> method) {
        return !Response.class.equals(method.getReturnType().getRawType());
    }
    
    static Type getResponseType(final Invokable<?, ?> method) {
        Type returnType;
        if (isAsynchronous(method)) {
            final Type[] genericArgs = getGenericTypeArguments(method.getReturnType());
            if (Response.class.equals(genericArgs[0])) {
                returnType = genericArgs[1];
            }
            else {
                returnType = genericArgs[0];
            }
        }
        else if (!bodyOnly(method)) {
            final Type[] genericArgs = getGenericTypeArguments(method.getReturnType());
            returnType = genericArgs[0];
        }
        else {
            returnType = method.getReturnType().getType();
        }
        return returnType;
    }
    
    public RestMethodInfo(final Method m) {
        this.pathArguments = new LinkedHashMap<Integer, String>();
        this.bodyArgument = Optional.absent();
        this.queryParameter = Optional.absent();
        this.parseMethod(Invokable.from(m));
    }
    
    public boolean isAsynchronous() {
        return this.asynchronous;
    }
    
    public boolean isBodyOnly() {
        return this.returnBodyOnly;
    }
    
    private void parseMethod(final Invokable<?, ?> method) {
        final Request request = method.getAnnotation(Request.class);
        this.urlTemplate = UrlTemplate.create(request.url());
        this.asynchronous = isAsynchronous(method);
        this.method = request.method();
        this.responseType = getResponseType(method);
        this.returnBodyOnly = bodyOnly(method);
        final ImmutableList<Parameter> methodParameters = method.getParameters();
        for (int i = 0; i < methodParameters.size(); ++i) {
            final Parameter parameter = methodParameters.get(i);
            if (parameter.isAnnotationPresent(Path.class)) {
                final Path path = parameter.getAnnotation(Path.class);
                assert path != null;
                Preconditions.checkState(this.urlTemplate.hasPathVariable(path.value()), "There is no path parameter with name '%s' declared in url template", path.value());
                this.pathArguments.put(i, path.value());
            }
            else if (parameter.isAnnotationPresent(Body.class)) {
                this.bodyArgument = Optional.of(i);
                if (parameter.isAnnotationPresent(Multipart.class)) {
                    Preconditions.checkArgument(TypeToken.of(MultiPartRequest.class).isSupertypeOf(parameter.getType()), "@Multipart parameters are expected to be MultiPartRequest. '%s' is not a MultiPartRequest", parameter.getType());
                    this.multiPart = true;
                }
            }
            else if (parameter.isAnnotationPresent(Query.class)) {
                Preconditions.checkArgument(TypeToken.of(Map.class).isSupertypeOf(parameter.getType()), "@Query parameters are expected to be maps. '%s' is not a Map", parameter.getType());
                this.queryParameter = Optional.of(i);
            }
        }
        this.validationPathArguments(method);
    }
    
    private void validationPathArguments(final Invokable<?, ?> method) {
        final Sets.SetView<String> difference = Sets.difference((Set<String>)Sets.newHashSet((Iterable<?>)this.urlTemplate.getPathVariables()), Sets.newHashSet((Iterable<?>)this.pathArguments.values()));
        Preconditions.checkState(difference.isEmpty(), "The following path arguments found in URL template, but not found in method signature: [%s]. Class: [%s]. Method [%s]. Did you forget @Path annotation?", Joiner.on(",").join(difference), method.getDeclaringClass().getSimpleName(), method.getName());
    }
    
    private String createUrl(final Object... args) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        for (final Map.Entry<Integer, String> pathVariables : this.pathArguments.entrySet()) {
            parameters.put(pathVariables.getValue(), args[pathVariables.getKey()]);
        }
        final UrlTemplate.Merger template = this.urlTemplate.merge().expand(parameters);
        if (this.queryParameter.isPresent()) {
            template.appendQueryParameters((Map<String, ?>)args[this.queryParameter.get()]);
        }
        return template.build();
    }
    
    private Object createBody(final Object... args) {
        return this.bodyArgument.isPresent() ? args[this.bodyArgument.get()] : null;
    }
    
    public <RQ, RS> RestCommand<RQ, RS> createRestCommand(final Object... args) {
        return new RestCommand<RQ, RS>(this.createUrl(args), this.method, (RQ)this.createBody(args), this.responseType, this.multiPart);
    }
    
    private static Type[] getGenericTypeArguments(final TypeToken<?> typeToken) {
        final Type rawType = typeToken.getType();
        Preconditions.checkArgument(rawType instanceof ParameterizedType, "Incorrect configuration. {} should be parameterized", rawType);
        return ((ParameterizedType)rawType).getActualTypeArguments();
    }
}
