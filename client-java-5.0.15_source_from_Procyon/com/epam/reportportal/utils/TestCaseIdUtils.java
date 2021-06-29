// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import java.util.Optional;
import com.epam.reportportal.service.item.TestCaseIdEntry;
import com.epam.reportportal.annotations.TestCaseId;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.epam.reportportal.annotations.TestCaseIdKey;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import javax.annotation.Nonnull;
import java.lang.reflect.Executable;
import java.util.List;
import java.util.function.Function;

public class TestCaseIdUtils
{
    private static final Function<List<?>, String> TRANSFORM_PARAMETERS;
    
    private TestCaseIdUtils() {
        throw new IllegalStateException("Static only class");
    }
    
    @Nonnull
    public static String getCodeRef(@Nonnull final Executable executable) {
        if (executable instanceof Constructor) {
            return executable.getName();
        }
        return executable.getDeclaringClass().getCanonicalName() + "." + executable.getName();
    }
    
    @Nullable
    public static <T> String getParametersForTestCaseId(final Executable executable, final List<T> parameters) {
        if (executable == null || parameters == null || parameters.isEmpty()) {
            return null;
        }
        final Annotation[][] parameterAnnotations = executable.getParameterAnnotations();
        final List<Integer> keys = new ArrayList<Integer>();
        for (int paramIndex = 0; paramIndex < parameterAnnotations.length; ++paramIndex) {
            for (int annotationIndex = 0; annotationIndex < parameterAnnotations[paramIndex].length; ++annotationIndex) {
                final Annotation testCaseIdAnnotation = parameterAnnotations[paramIndex][annotationIndex];
                if (testCaseIdAnnotation.annotationType() == TestCaseIdKey.class) {
                    keys.add(paramIndex);
                }
            }
        }
        if (keys.isEmpty()) {
            return TestCaseIdUtils.TRANSFORM_PARAMETERS.apply(parameters);
        }
        if (keys.size() <= 1) {
            return String.valueOf(parameters.get(keys.get(0)));
        }
        return TestCaseIdUtils.TRANSFORM_PARAMETERS.apply(keys.stream().map((Function<? super Object, ?>)parameters::get).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
    }
    
    @Nullable
    public static <T> TestCaseIdEntry getTestCaseId(@Nullable final TestCaseId annotation, @Nullable final Executable executable, @Nullable final List<T> parameters) {
        return getTestCaseId(annotation, executable, null, parameters);
    }
    
    @Nullable
    public static <T> TestCaseIdEntry getTestCaseId(@Nullable final TestCaseId annotation, @Nullable final Executable executable, @Nullable final String codRef, @Nullable final List<T> parameters) {
        if (annotation == null) {
            return Optional.ofNullable(codRef).map(c -> getTestCaseId(c, parameters)).orElse(getTestCaseId(executable, parameters));
        }
        if (annotation.value().isEmpty()) {
            if (annotation.parametrized()) {
                return Optional.ofNullable(getParametersForTestCaseId(executable, parameters)).map((Function<? super String, ? extends TestCaseIdEntry>)TestCaseIdEntry::new).orElse(Optional.ofNullable(codRef).map(c -> getTestCaseId(c, parameters)).orElse(getTestCaseId(executable, parameters)));
            }
            return Optional.ofNullable(codRef).map(c -> getTestCaseId(c, parameters)).orElse(getTestCaseId(executable, parameters));
        }
        else {
            if (annotation.parametrized()) {
                String string;
                final Object o;
                final StringBuilder sb;
                return Optional.ofNullable(getParametersForTestCaseId(executable, parameters)).map(p -> {
                    // new(com.epam.reportportal.service.item.TestCaseIdEntry.class)
                    new StringBuilder().append(annotation.value());
                    if (p.startsWith("[")) {
                        string = p;
                    }
                    else {
                        string = "[" + p + "]";
                    }
                    new TestCaseIdEntry(sb.append(string).toString());
                    return o;
                }).orElse(Optional.ofNullable(codRef).map(c -> getTestCaseId(c, parameters)).orElse(getTestCaseId(executable, parameters)));
            }
            return new TestCaseIdEntry(annotation.value());
        }
    }
    
    @Nullable
    public static <T> TestCaseIdEntry getTestCaseId(@Nullable final Executable executable, @Nullable final List<T> parameters) {
        return Optional.ofNullable(executable).map(m -> getTestCaseId(getCodeRef(m), parameters)).orElse(getTestCaseId(parameters));
    }
    
    @Nullable
    public static <T> TestCaseIdEntry getTestCaseId(@Nullable final String codeRef, @Nullable final List<T> parameters) {
        final TestCaseIdEntry testCaseIdEntry;
        return Optional.ofNullable(codeRef).map(r -> {
            new TestCaseIdEntry(codeRef + Optional.ofNullable(parameters).map((Function<? super List<T>, ? extends String>)TestCaseIdUtils.TRANSFORM_PARAMETERS).orElse(""));
            return testCaseIdEntry;
        }).orElse(getTestCaseId(parameters));
    }
    
    @Nullable
    public static <T> TestCaseIdEntry getTestCaseId(@Nullable final List<T> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        return new TestCaseIdEntry(TestCaseIdUtils.TRANSFORM_PARAMETERS.apply(parameters));
    }
    
    static {
        TRANSFORM_PARAMETERS = (it -> it.stream().map((Function<? super Object, ?>)String::valueOf).collect((Collector<? super Object, ?, String>)Collectors.joining(",", "[", "]")));
    }
}
