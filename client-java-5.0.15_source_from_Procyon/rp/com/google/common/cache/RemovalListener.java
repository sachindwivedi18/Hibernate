// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import rp.com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface RemovalListener<K, V>
{
    void onRemoval(final RemovalNotification<K, V> p0);
}
