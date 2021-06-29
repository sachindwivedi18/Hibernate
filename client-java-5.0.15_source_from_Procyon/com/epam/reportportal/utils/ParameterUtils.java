// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.epam.reportportal.annotations.ParameterKey;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.Collections;
import java.util.Optional;
import com.epam.ta.reportportal.ws.model.ParameterResource;
import javax.annotation.Nullable;
import java.util.List;
import javax.annotation.Nonnull;
import java.lang.reflect.Executable;

public class ParameterUtils
{
    public static final String NULL_VALUE = "NULL";
    
    private ParameterUtils() {
    }
    
    @Nonnull
    public static <T> List<ParameterResource> getParameters(@Nonnull final Executable method, @Nullable final List<T> parameterValues) {
        final List<?> values = Optional.ofNullable(parameterValues).orElse(Collections.emptyList());
        final Parameter[] params = method.getParameters();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final ParameterResource res;
        final Object o;
        final Object o2;
        final String parameterName;
        final List<T> list;
        return IntStream.range(0, params.length).boxed().map(i -> {
            res = new ParameterResource();
            parameterName = Arrays.stream(o[i]).filter(a -> ParameterKey.class.equals(a.annotationType())).map(a -> a.value()).findFirst().orElseGet(() -> o2[i].getType().getName());
            res.setKey(parameterName);
            res.setValue((String)Optional.ofNullable((Object)((i < list.size()) ? list.get(i) : null)).map((Function<? super Object, ?>)String::valueOf).orElse("NULL"));
            return res;
        }).collect((Collector<? super Object, ?, List<ParameterResource>>)Collectors.toList());
    }
    
    public static Class<?> toBoxedType(@Nonnull final Class<?> primitiveType) {
        if (!primitiveType.isPrimitive()) {
            return primitiveType;
        }
        if (primitiveType == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveType == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveType == Character.TYPE) {
            return Character.class;
        }
        if (primitiveType == Short.TYPE) {
            return Short.class;
        }
        if (primitiveType == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveType == Long.TYPE) {
            return Long.class;
        }
        if (primitiveType == Float.TYPE) {
            return Float.class;
        }
        if (primitiveType == Double.TYPE) {
            return Double.class;
        }
        if (primitiveType == Void.TYPE) {
            return Void.class;
        }
        return null;
    }
    
    @Nonnull
    private static <T> List<ParameterResource> getParameters(@Nullable final List<Pair<String, T>> arguments) {
        final ParameterResource p;
        return Optional.ofNullable(arguments).map(args -> args.stream().map(a -> {
            p = new ParameterResource();
            p.setKey((String)a.getKey());
            p.setValue((String)Optional.ofNullable(a.getValue()).map((Function<? super Object, ?>)String::valueOf).orElse("NULL"));
            return p;
        }).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList())).orElse(Collections.emptyList());
    }
    
    @Nonnull
    public static <T> List<ParameterResource> getParameters(@Nullable final String codeRef, @Nullable final List<Pair<String, T>> parameters) {
        final Optional<List<Object>> paramValues = Optional.ofNullable(parameters).map(args -> args.stream().map(a -> a.getValue()).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
        final int lastDelimiterIndex;
        final String className;
        final String methodName;
        Optional<Class<?>> testStepClass;
        final Optional<Object> optional;
        return Optional.ofNullable(codeRef).flatMap(cr -> {
            lastDelimiterIndex = cr.lastIndexOf(46);
            className = cr.substring(0, lastDelimiterIndex);
            methodName = cr.substring(lastDelimiterIndex + 1);
            try {
                testStepClass = Optional.of(Class.forName(className));
            }
            catch (ClassNotFoundException e1) {
                try {
                    testStepClass = Optional.of(Class.forName(cr));
                }
                catch (ClassNotFoundException e2) {
                    testStepClass = Optional.empty();
                }
            }
            return testStepClass.flatMap(cl -> Stream.concat((Stream<?>)Arrays.stream((T[])cl.getDeclaredMethods()), (Stream<?>)Arrays.stream((T[])cl.getDeclaredConstructors())).filter(m -> methodName.equals(m.getName()) || cr.equals(m.getName())).filter(m -> m.getParameterCount() == optional.map((Function<? super Object, ?>)List::size).orElse(0)).findAny());
        }).map(m -> getParameters(m, paramValues.orElse(null))).orElse(getParameters(parameters));
    }
}
