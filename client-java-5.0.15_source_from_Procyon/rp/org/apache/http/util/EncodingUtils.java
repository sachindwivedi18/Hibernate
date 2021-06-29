// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.util;

import rp.org.apache.http.Consts;
import java.io.UnsupportedEncodingException;

public final class EncodingUtils
{
    public static String getString(final byte[] data, final int offset, final int length, final String charset) {
        Args.notNull(data, "Input");
        Args.notEmpty(charset, "Charset");
        try {
            return new String(data, offset, length, charset);
        }
        catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }
    
    public static String getString(final byte[] data, final String charset) {
        Args.notNull(data, "Input");
        return getString(data, 0, data.length, charset);
    }
    
    public static byte[] getBytes(final String data, final String charset) {
        Args.notNull(data, "Input");
        Args.notEmpty(charset, "Charset");
        try {
            return data.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }
    
    public static byte[] getAsciiBytes(final String data) {
        Args.notNull(data, "Input");
        return data.getBytes(Consts.ASCII);
    }
    
    public static String getAsciiString(final byte[] data, final int offset, final int length) {
        Args.notNull(data, "Input");
        return new String(data, offset, length, Consts.ASCII);
    }
    
    public static String getAsciiString(final byte[] data) {
        Args.notNull(data, "Input");
        return getAsciiString(data, 0, data.length);
    }
    
    private EncodingUtils() {
    }
}
