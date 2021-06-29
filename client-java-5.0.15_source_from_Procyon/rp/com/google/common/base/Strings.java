// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import java.util.logging.Level;
import java.util.logging.Logger;
import rp.com.google.common.annotations.VisibleForTesting;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Strings
{
    private Strings() {
    }
    
    public static String nullToEmpty(final String string) {
        return Platform.nullToEmpty(string);
    }
    
    public static String emptyToNull(final String string) {
        return Platform.emptyToNull(string);
    }
    
    public static boolean isNullOrEmpty(final String string) {
        return Platform.stringIsNullOrEmpty(string);
    }
    
    public static String padStart(final String string, final int minLength, final char padChar) {
        Preconditions.checkNotNull(string);
        if (string.length() >= minLength) {
            return string;
        }
        final StringBuilder sb = new StringBuilder(minLength);
        for (int i = string.length(); i < minLength; ++i) {
            sb.append(padChar);
        }
        sb.append(string);
        return sb.toString();
    }
    
    public static String padEnd(final String string, final int minLength, final char padChar) {
        Preconditions.checkNotNull(string);
        if (string.length() >= minLength) {
            return string;
        }
        final StringBuilder sb = new StringBuilder(minLength);
        sb.append(string);
        for (int i = string.length(); i < minLength; ++i) {
            sb.append(padChar);
        }
        return sb.toString();
    }
    
    public static String repeat(final String string, final int count) {
        Preconditions.checkNotNull(string);
        if (count <= 1) {
            Preconditions.checkArgument(count >= 0, "invalid count: %s", count);
            return (count == 0) ? "" : string;
        }
        final int len = string.length();
        final long longSize = len * (long)count;
        final int size = (int)longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
        }
        final char[] array = new char[size];
        string.getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }
    
    public static String commonPrefix(final CharSequence a, final CharSequence b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        int maxPrefixLength;
        int p;
        for (maxPrefixLength = Math.min(a.length(), b.length()), p = 0; p < maxPrefixLength && a.charAt(p) == b.charAt(p); ++p) {}
        if (validSurrogatePairAt(a, p - 1) || validSurrogatePairAt(b, p - 1)) {
            --p;
        }
        return a.subSequence(0, p).toString();
    }
    
    public static String commonSuffix(final CharSequence a, final CharSequence b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        int maxSuffixLength;
        int s;
        for (maxSuffixLength = Math.min(a.length(), b.length()), s = 0; s < maxSuffixLength && a.charAt(a.length() - s - 1) == b.charAt(b.length() - s - 1); ++s) {}
        if (validSurrogatePairAt(a, a.length() - s - 1) || validSurrogatePairAt(b, b.length() - s - 1)) {
            --s;
        }
        return a.subSequence(a.length() - s, a.length()).toString();
    }
    
    @VisibleForTesting
    static boolean validSurrogatePairAt(final CharSequence string, final int index) {
        return index >= 0 && index <= string.length() - 2 && Character.isHighSurrogate(string.charAt(index)) && Character.isLowSurrogate(string.charAt(index + 1));
    }
    
    public static String lenientFormat(String template, Object... args) {
        template = String.valueOf(template);
        if (args == null) {
            args = new Object[] { "(Object[])null" };
        }
        else {
            for (int i = 0; i < args.length; ++i) {
                args[i] = lenientToString(args[i]);
            }
        }
        final StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int j = 0;
        while (j < args.length) {
            final int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[j++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());
        if (j < args.length) {
            builder.append(" [");
            builder.append(args[j++]);
            while (j < args.length) {
                builder.append(", ");
                builder.append(args[j++]);
            }
            builder.append(']');
        }
        return builder.toString();
    }
    
    private static String lenientToString(final Object o) {
        try {
            return String.valueOf(o);
        }
        catch (Exception e) {
            final String objectToString = o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
            Logger.getLogger("rp.com.google.common.base.Strings").log(Level.WARNING, "Exception during lenientFormat for " + objectToString, e);
            return "<" + objectToString + " threw " + e.getClass().getName() + ">";
        }
    }
}
