// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import java.util.Arrays;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Objects extends ExtraObjectsMethodsForWeb
{
    private Objects() {
    }
    
    public static boolean equal(final Object a, final Object b) {
        return a == b || (a != null && a.equals(b));
    }
    
    public static int hashCode(final Object... objects) {
        return Arrays.hashCode(objects);
    }
}
