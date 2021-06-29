// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Accessible
{
    private final Object object;
    
    public Accessible(final Object object) {
        this.object = object;
    }
    
    public AccessibleMethod method(final Method m) {
        return new AccessibleMethod(this.object, m);
    }
    
    public AccessibleField field(final Field f) {
        return new AccessibleField(this.object, f);
    }
    
    public AccessibleField field(final String name) throws NoSuchFieldException {
        return new AccessibleField(this.object, this.getField(name));
    }
    
    public static Accessible on(final Object object) {
        return new Accessible(object);
    }
    
    private Field getField(final String fieldName) throws NoSuchFieldException {
        Class<?> clazz = this.object.getClass();
        try {
            return clazz.getField(fieldName);
        }
        catch (NoSuchFieldException e) {
            try {
                return clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    throw e;
                }
                return clazz.getDeclaredField(fieldName);
            }
        }
    }
}
