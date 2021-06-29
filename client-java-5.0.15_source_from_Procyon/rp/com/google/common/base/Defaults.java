// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public final class Defaults
{
    private static final Double DOUBLE_DEFAULT;
    private static final Float FLOAT_DEFAULT;
    
    private Defaults() {
    }
    
    public static <T> T defaultValue(final Class<T> type) {
        Preconditions.checkNotNull(type);
        if (type == Boolean.TYPE) {
            return (T)Boolean.FALSE;
        }
        if (type == Character.TYPE) {
            return (T)Character.valueOf('\0');
        }
        if (type == Byte.TYPE) {
            return (T)0;
        }
        if (type == Short.TYPE) {
            return (T)0;
        }
        if (type == Integer.TYPE) {
            return (T)Integer.valueOf(0);
        }
        if (type == Long.TYPE) {
            return (T)Long.valueOf(0L);
        }
        if (type == Float.TYPE) {
            return (T)Defaults.FLOAT_DEFAULT;
        }
        if (type == Double.TYPE) {
            return (T)Defaults.DOUBLE_DEFAULT;
        }
        return null;
    }
    
    static {
        DOUBLE_DEFAULT = 0.0;
        FLOAT_DEFAULT = 0.0f;
    }
}
