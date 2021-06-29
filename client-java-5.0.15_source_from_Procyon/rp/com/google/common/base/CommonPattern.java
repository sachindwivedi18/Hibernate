// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class CommonPattern
{
    public abstract CommonMatcher matcher(final CharSequence p0);
    
    public abstract String pattern();
    
    public abstract int flags();
    
    @Override
    public abstract String toString();
    
    public static CommonPattern compile(final String pattern) {
        return Platform.compilePattern(pattern);
    }
    
    public static boolean isPcreLike() {
        return Platform.patternCompilerIsPcreLike();
    }
}
