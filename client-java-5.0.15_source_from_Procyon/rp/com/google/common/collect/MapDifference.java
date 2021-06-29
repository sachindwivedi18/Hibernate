// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Map;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface MapDifference<K, V>
{
    boolean areEqual();
    
    Map<K, V> entriesOnlyOnLeft();
    
    Map<K, V> entriesOnlyOnRight();
    
    Map<K, V> entriesInCommon();
    
    Map<K, ValueDifference<V>> entriesDiffering();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    public interface ValueDifference<V>
    {
        V leftValue();
        
        V rightValue();
        
        boolean equals(final Object p0);
        
        int hashCode();
    }
}
