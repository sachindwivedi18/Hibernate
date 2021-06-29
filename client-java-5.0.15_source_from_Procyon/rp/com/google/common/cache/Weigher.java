// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Weigher<K, V>
{
    int weigh(final K p0, final V p1);
}
