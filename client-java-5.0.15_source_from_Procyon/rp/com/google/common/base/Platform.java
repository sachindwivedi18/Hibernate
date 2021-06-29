// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.ServiceConfigurationError;
import java.util.Locale;
import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    private static final Logger logger;
    private static final PatternCompiler patternCompiler;
    
    private Platform() {
    }
    
    static long systemNanoTime() {
        return System.nanoTime();
    }
    
    static CharMatcher precomputeCharMatcher(final CharMatcher matcher) {
        return matcher.precomputedInternal();
    }
    
    static <T extends Enum<T>> Optional<T> getEnumIfPresent(final Class<T> enumClass, final String value) {
        final WeakReference<? extends Enum<?>> ref = Enums.getEnumConstants(enumClass).get(value);
        return (ref == null) ? Optional.absent() : Optional.of(enumClass.cast(ref.get()));
    }
    
    static String formatCompact4Digits(final double value) {
        return String.format(Locale.ROOT, "%.4g", value);
    }
    
    static boolean stringIsNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
    
    static String nullToEmpty(final String string) {
        return (string == null) ? "" : string;
    }
    
    static String emptyToNull(final String string) {
        return stringIsNullOrEmpty(string) ? null : string;
    }
    
    static CommonPattern compilePattern(final String pattern) {
        Preconditions.checkNotNull(pattern);
        return Platform.patternCompiler.compile(pattern);
    }
    
    static boolean patternCompilerIsPcreLike() {
        return Platform.patternCompiler.isPcreLike();
    }
    
    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler();
    }
    
    private static void logPatternCompilerError(final ServiceConfigurationError e) {
        Platform.logger.log(Level.WARNING, "Error loading regex compiler, falling back to next option", e);
    }
    
    static {
        logger = Logger.getLogger(Platform.class.getName());
        patternCompiler = loadPatternCompiler();
    }
    
    private static final class JdkPatternCompiler implements PatternCompiler
    {
        @Override
        public CommonPattern compile(final String pattern) {
            return new JdkPattern(Pattern.compile(pattern));
        }
        
        @Override
        public boolean isPcreLike() {
            return true;
        }
    }
}
