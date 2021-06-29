// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Verify
{
    public static void verify(final boolean expression) {
        if (!expression) {
            throw new VerifyException();
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final char p1) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final int p1) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final long p1) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final char p1, final char p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final int p1, final char p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final long p1, final char p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final char p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final char p1, final int p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final int p1, final int p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final long p1, final int p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final int p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final char p1, final long p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final int p1, final long p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final long p1, final long p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final long p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final char p1, final Object p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final int p1, final Object p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final long p1, final Object p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final Object p2) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final Object p2, final Object p3) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2, p3));
        }
    }
    
    public static void verify(final boolean expression, final String errorMessageTemplate, final Object p1, final Object p2, final Object p3, final Object p4) {
        if (!expression) {
            throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, p1, p2, p3, p4));
        }
    }
    
    @CanIgnoreReturnValue
    public static <T> T verifyNotNull(final T reference) {
        return verifyNotNull(reference, "expected a non-null reference", new Object[0]);
    }
    
    @CanIgnoreReturnValue
    public static <T> T verifyNotNull(final T reference, final String errorMessageTemplate, final Object... errorMessageArgs) {
        verify(reference != null, errorMessageTemplate, errorMessageArgs);
        return reference;
    }
    
    private Verify() {
    }
}
