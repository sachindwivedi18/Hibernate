// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import java.util.Set;
import rp.com.google.common.annotations.Beta;

@Beta
public abstract class AbstractGraph<N> extends AbstractBaseGraph<N> implements Graph<N>
{
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Graph)) {
            return false;
        }
        final Graph<?> other = (Graph<?>)obj;
        return this.isDirected() == other.isDirected() && this.nodes().equals(other.nodes()) && this.edges().equals(other.edges());
    }
    
    @Override
    public final int hashCode() {
        return this.edges().hashCode();
    }
    
    @Override
    public String toString() {
        return "isDirected: " + this.isDirected() + ", allowsSelfLoops: " + this.allowsSelfLoops() + ", nodes: " + this.nodes() + ", edges: " + this.edges();
    }
}
