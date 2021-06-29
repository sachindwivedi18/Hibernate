// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AccessibleMethod
{
    private final Method method;
    private final Object bean;
    
    AccessibleMethod(final Object bean, final Method method) {
        this.bean = bean;
        this.method = method;
    }
    
    public Object invoke(final Object... args) throws Throwable {
        try {
            return this.invoke(this.bean, this.method, args);
        }
        catch (IllegalAccessException accessException) {
            this.method.setAccessible(true);
            try {
                return this.invoke(this.bean, this.method, args);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }
    
    private Object invoke(final Object bean, final Method m, final Object... args) throws Throwable {
        try {
            return m.invoke(bean, args);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e2) {
            throw e2.getTargetException();
        }
    }
}
