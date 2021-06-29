// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

interface GraphConnections<N, V>
{
    Set<N> adjacentNodes();
    
    Set<N> predecessors();
    
    Set<N> successors();
    
    V value(final N p0);
    
    void removePredecessor(final N p0);
    
    @CanIgnoreReturnValue
    V removeSuccessor(final N p0);
    
    void addPredecessor(final N p0, final V p1);
    
    @CanIgnoreReturnValue
    V addSuccessor(final N p0, final V p1);
}
