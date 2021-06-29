// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.collect.Maps;
import rp.com.google.common.base.Function;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import rp.com.google.common.annotations.Beta;

@Beta
public abstract class AbstractValueGraph<N, V> extends AbstractBaseGraph<N> implements ValueGraph<N, V>
{
    @Override
    public Graph<N> asGraph() {
        return new AbstractGraph<N>() {
            @Override
            public Set<N> nodes() {
                return AbstractValueGraph.this.nodes();
            }
            
            @Override
            public Set<EndpointPair<N>> edges() {
                return (Set<EndpointPair<N>>)AbstractValueGraph.this.edges();
            }
            
            @Override
            public boolean isDirected() {
                return AbstractValueGraph.this.isDirected();
            }
            
            @Override
            public boolean allowsSelfLoops() {
                return AbstractValueGraph.this.allowsSelfLoops();
            }
            
            @Override
            public ElementOrder<N> nodeOrder() {
                return AbstractValueGraph.this.nodeOrder();
            }
            
            @Override
            public Set<N> adjacentNodes(final N node) {
                return AbstractValueGraph.this.adjacentNodes(node);
            }
            
            @Override
            public Set<N> predecessors(final N node) {
                return AbstractValueGraph.this.predecessors(node);
            }
            
            @Override
            public Set<N> successors(final N node) {
                return AbstractValueGraph.this.successors(node);
            }
            
            @Override
            public int degree(final N node) {
                return AbstractValueGraph.this.degree(node);
            }
            
            @Override
            public int inDegree(final N node) {
                return AbstractValueGraph.this.inDegree(node);
            }
            
            @Override
            public int outDegree(final N node) {
                return AbstractValueGraph.this.outDegree(node);
            }
        };
    }
    
    @Override
    public Optional<V> edgeValue(final N nodeU, final N nodeV) {
        return Optional.ofNullable(this.edgeValueOrDefault(nodeU, nodeV, null));
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueGraph)) {
            return false;
        }
        final ValueGraph<?, ?> other = (ValueGraph<?, ?>)obj;
        return this.isDirected() == other.isDirected() && this.nodes().equals(other.nodes()) && edgeValueMap((ValueGraph<Object, Object>)this).equals(edgeValueMap(other));
    }
    
    @Override
    public final int hashCode() {
        return edgeValueMap((ValueGraph<Object, Object>)this).hashCode();
    }
    
    @Override
    public String toString() {
        return "isDirected: " + this.isDirected() + ", allowsSelfLoops: " + this.allowsSelfLoops() + ", nodes: " + this.nodes() + ", edges: " + edgeValueMap((ValueGraph<Object, Object>)this);
    }
    
    private static <N, V> Map<EndpointPair<N>, V> edgeValueMap(final ValueGraph<N, V> graph) {
        final Function<EndpointPair<N>, V> edgeToValueFn = new Function<EndpointPair<N>, V>() {
            @Override
            public V apply(final EndpointPair<N> edge) {
                return graph.edgeValueOrDefault(edge.nodeU(), edge.nodeV(), null);
            }
        };
        return Maps.asMap(graph.edges(), edgeToValueFn);
    }
}
