// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Table<R, C, V>
{
    boolean contains(@CompatibleWith("R") final Object p0, @CompatibleWith("C") final Object p1);
    
    boolean containsRow(@CompatibleWith("R") final Object p0);
    
    boolean containsColumn(@CompatibleWith("C") final Object p0);
    
    boolean containsValue(@CompatibleWith("V") final Object p0);
    
    V get(@CompatibleWith("R") final Object p0, @CompatibleWith("C") final Object p1);
    
    boolean isEmpty();
    
    int size();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    void clear();
    
    @CanIgnoreReturnValue
    V put(final R p0, final C p1, final V p2);
    
    void putAll(final Table<? extends R, ? extends C, ? extends V> p0);
    
    @CanIgnoreReturnValue
    V remove(@CompatibleWith("R") final Object p0, @CompatibleWith("C") final Object p1);
    
    Map<C, V> row(final R p0);
    
    Map<R, V> column(final C p0);
    
    Set<Cell<R, C, V>> cellSet();
    
    Set<R> rowKeySet();
    
    Set<C> columnKeySet();
    
    Collection<V> values();
    
    Map<R, Map<C, V>> rowMap();
    
    Map<C, Map<R, V>> columnMap();
    
    public interface Cell<R, C, V>
    {
        R getRowKey();
        
        C getColumnKey();
        
        V getValue();
        
        boolean equals(final Object p0);
        
        int hashCode();
    }
}
