// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import java.util.Set;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public interface BiMap<K, V> extends Map<K, V>
{
    @CanIgnoreReturnValue
    V put(final K p0, final V p1);
    
    @CanIgnoreReturnValue
    V forcePut(final K p0, final V p1);
    
    void putAll(final Map<? extends K, ? extends V> p0);
    
    Set<V> values();
    
    BiMap<V, K> inverse();
}
