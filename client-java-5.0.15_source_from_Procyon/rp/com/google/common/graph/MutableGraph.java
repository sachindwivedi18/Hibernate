// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.Beta;

@Beta
public interface MutableGraph<N> extends Graph<N>
{
    @CanIgnoreReturnValue
    boolean addNode(final N p0);
    
    @CanIgnoreReturnValue
    boolean putEdge(final N p0, final N p1);
    
    @CanIgnoreReturnValue
    boolean removeNode(final N p0);
    
    @CanIgnoreReturnValue
    boolean removeEdge(final N p0, final N p1);
}
