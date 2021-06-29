// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.collect.Sets;
import rp.com.google.common.collect.ImmutableSet;
import rp.com.google.common.collect.Iterators;
import rp.com.google.common.base.Function;
import rp.com.google.common.math.IntMath;
import rp.com.google.common.primitives.Ints;
import rp.com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;
import rp.com.google.common.base.Preconditions;

abstract class AbstractBaseGraph<N> implements BaseGraph<N>
{
    protected long edgeCount() {
        long degreeSum = 0L;
        for (final N node : this.nodes()) {
            degreeSum += this.degree(node);
        }
        Preconditions.checkState((degreeSum & 0x1L) == 0x0L);
        return degreeSum >>> 1;
    }
    
    @Override
    public Set<EndpointPair<N>> edges() {
        return new AbstractSet<EndpointPair<N>>() {
            @Override
            public UnmodifiableIterator<EndpointPair<N>> iterator() {
                return (UnmodifiableIterator<EndpointPair<N>>)EndpointPairIterator.of((BaseGraph<Object>)AbstractBaseGraph.this);
            }
            
            @Override
            public int size() {
                return Ints.saturatedCast(AbstractBaseGraph.this.edgeCount());
            }
            
            @Override
            public boolean remove(final Object o) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean contains(final Object obj) {
                if (!(obj instanceof EndpointPair)) {
                    return false;
                }
                final EndpointPair<?> endpointPair = (EndpointPair<?>)obj;
                return AbstractBaseGraph.this.isDirected() == endpointPair.isOrdered() && AbstractBaseGraph.this.nodes().contains(endpointPair.nodeU()) && AbstractBaseGraph.this.successors(endpointPair.nodeU()).contains(endpointPair.nodeV());
            }
        };
    }
    
    @Override
    public Set<EndpointPair<N>> incidentEdges(final N node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(this.nodes().contains(node), "Node %s is not an element of this graph.", node);
        return (Set<EndpointPair<N>>)IncidentEdgeSet.of(this, node);
    }
    
    @Override
    public int degree(final N node) {
        if (this.isDirected()) {
            return IntMath.saturatedAdd(this.predecessors(node).size(), this.successors(node).size());
        }
        final Set<N> neighbors = this.adjacentNodes(node);
        final int selfLoopCount = (this.allowsSelfLoops() && neighbors.contains(node)) ? 1 : 0;
        return IntMath.saturatedAdd(neighbors.size(), selfLoopCount);
    }
    
    @Override
    public int inDegree(final N node) {
        return this.isDirected() ? this.predecessors(node).size() : this.degree(node);
    }
    
    @Override
    public int outDegree(final N node) {
        return this.isDirected() ? this.successors(node).size() : this.degree(node);
    }
    
    @Override
    public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        return this.nodes().contains(nodeU) && this.successors(nodeU).contains(nodeV);
    }
    
    private abstract static class IncidentEdgeSet<N> extends AbstractSet<EndpointPair<N>>
    {
        protected final N node;
        protected final BaseGraph<N> graph;
        
        public static <N> IncidentEdgeSet<N> of(final BaseGraph<N> graph, final N node) {
            return (IncidentEdgeSet<N>)(graph.isDirected() ? new Directed<Object>((BaseGraph)graph, (Object)node) : new Undirected<Object>((BaseGraph)graph, (Object)node));
        }
        
        private IncidentEdgeSet(final BaseGraph<N> graph, final N node) {
            this.graph = graph;
            this.node = node;
        }
        
        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        private static final class Directed<N> extends IncidentEdgeSet<N>
        {
            private Directed(final BaseGraph<N> graph, final N node) {
                super((BaseGraph)graph, (Object)node);
            }
            
            @Override
            public UnmodifiableIterator<EndpointPair<N>> iterator() {
                return Iterators.unmodifiableIterator(Iterators.concat(Iterators.transform(this.graph.predecessors(this.node).iterator(), (Function<? super N, ? extends EndpointPair<N>>)new Function<N, EndpointPair<N>>() {
                    @Override
                    public EndpointPair<N> apply(final N predecessor) {
                        return EndpointPair.ordered(predecessor, Directed.this.node);
                    }
                }), Iterators.transform((Iterator<N>)Sets.difference(this.graph.successors(this.node), ImmutableSet.of(this.node)).iterator(), (Function<? super N, ? extends EndpointPair<N>>)new Function<N, EndpointPair<N>>() {
                    @Override
                    public EndpointPair<N> apply(final N successor) {
                        return EndpointPair.ordered(Directed.this.node, successor);
                    }
                })));
            }
            
            @Override
            public int size() {
                return this.graph.inDegree(this.node) + this.graph.outDegree(this.node) - (this.graph.successors(this.node).contains(this.node) ? 1 : 0);
            }
            
            @Override
            public boolean contains(final Object obj) {
                if (!(obj instanceof EndpointPair)) {
                    return false;
                }
                final EndpointPair<?> endpointPair = (EndpointPair<?>)obj;
                if (!endpointPair.isOrdered()) {
                    return false;
                }
                final Object source = endpointPair.source();
                final Object target = endpointPair.target();
                return (this.node.equals(source) && this.graph.successors(this.node).contains(target)) || (this.node.equals(target) && this.graph.predecessors(this.node).contains(source));
            }
        }
        
        private static final class Undirected<N> extends IncidentEdgeSet<N>
        {
            private Undirected(final BaseGraph<N> graph, final N node) {
                super((BaseGraph)graph, (Object)node);
            }
            
            @Override
            public UnmodifiableIterator<EndpointPair<N>> iterator() {
                return Iterators.unmodifiableIterator(Iterators.transform(this.graph.adjacentNodes(this.node).iterator(), (Function<? super N, ? extends EndpointPair<N>>)new Function<N, EndpointPair<N>>() {
                    @Override
                    public EndpointPair<N> apply(final N adjacentNode) {
                        return EndpointPair.unordered(Undirected.this.node, adjacentNode);
                    }
                }));
            }
            
            @Override
            public int size() {
                return this.graph.adjacentNodes(this.node).size();
            }
            
            @Override
            public boolean contains(final Object obj) {
                if (!(obj instanceof EndpointPair)) {
                    return false;
                }
                final EndpointPair<?> endpointPair = (EndpointPair<?>)obj;
                if (endpointPair.isOrdered()) {
                    return false;
                }
                final Set<N> adjacent = this.graph.adjacentNodes(this.node);
                final Object nodeU = endpointPair.nodeU();
                final Object nodeV = endpointPair.nodeV();
                return (this.node.equals(nodeV) && adjacent.contains(nodeU)) || (this.node.equals(nodeU) && adjacent.contains(nodeV));
            }
        }
    }
}
