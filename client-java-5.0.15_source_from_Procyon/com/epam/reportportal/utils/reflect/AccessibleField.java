// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.reflect;

import java.lang.reflect.Field;

public class AccessibleField
{
    private final Field f;
    private final Object bean;
    
    AccessibleField(final Object bean, final Field f) {
        this.bean = bean;
        this.f = f;
    }
    
    public Class<?> getType() {
        return this.f.getType();
    }
    
    public void setValue(final Object value) {
        try {
            this.f.set(this.bean, value);
        }
        catch (IllegalAccessException accessException) {
            this.f.setAccessible(true);
            try {
                this.f.set(this.bean, value);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }
    
    public Object getValue() {
        try {
            return this.f.get(this.bean);
        }
        catch (IllegalAccessException accessException) {
            this.f.setAccessible(true);
            try {
                return this.f.get(this.bean);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }
}
