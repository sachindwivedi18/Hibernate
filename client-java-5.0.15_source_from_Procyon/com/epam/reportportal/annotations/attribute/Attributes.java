// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.annotations.attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Attributes {
    Attribute[] attributes() default {};
    
    AttributeValue[] attributeValues() default {};
    
    MultiKeyAttribute[] multiKeyAttributes() default {};
    
    MultiValueAttribute[] multiValueAttributes() default {};
}
