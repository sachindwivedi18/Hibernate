// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface Contract {
    ThreadingBehavior threading() default ThreadingBehavior.UNSAFE;
}
