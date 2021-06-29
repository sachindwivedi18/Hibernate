// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface StepTemplateConfig {
    public static final String METHOD_NAME_TEMPLATE = "method";
    public static final String ITERABLE_START_PATTERN = "[";
    public static final String ITERABLE_END_PATTERN = "]";
    public static final String ITERABLE_ELEMENT_DELIMITER = ", ";
    public static final String ARRAY_START_PATTERN = "{";
    public static final String ARRAY_END_PATTERN = "}";
    public static final String ARRAY_ELEMENT_DELIMITER = ", ";
    
    String methodNameTemplate() default "method";
    
    String iterableStartSymbol() default "[";
    
    String iterableEndSymbol() default "]";
    
    String iterableElementDelimiter() default ", ";
    
    String arrayStartSymbol() default "{";
    
    String arrayEndSymbol() default "}";
    
    String arrayElementDelimiter() default ", ";
}
