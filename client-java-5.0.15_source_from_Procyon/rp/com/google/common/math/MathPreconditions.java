// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.math;

import java.math.BigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
@CanIgnoreReturnValue
final class MathPreconditions
{
    static int checkPositive(final String role, final int x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }
    
    static long checkPositive(final String role, final long x) {
        if (x <= 0L) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }
    
    static BigInteger checkPositive(final String role, final BigInteger x) {
        if (x.signum() <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }
    
    static int checkNonNegative(final String role, final int x) {
        if (x < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }
    
    static long checkNonNegative(final String role, final long x) {
        if (x < 0L) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }
    
    static BigInteger checkNonNegative(final String role, final BigInteger x) {
        if (x.signum() < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }
    
    static double checkNonNegative(final String role, final double x) {
        if (x < 0.0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }
    
    static void checkRoundingUnnecessary(final boolean condition) {
        if (!condition) {
            throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
        }
    }
    
    static void checkInRange(final boolean condition) {
        if (!condition) {
            throw new ArithmeticException("not in range");
        }
    }
    
    static void checkNoOverflow(final boolean condition, final String methodName, final int a, final int b) {
        if (!condition) {
            throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
        }
    }
    
    static void checkNoOverflow(final boolean condition, final String methodName, final long a, final long b) {
        if (!condition) {
            throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
        }
    }
    
    private MathPreconditions() {
    }
}
