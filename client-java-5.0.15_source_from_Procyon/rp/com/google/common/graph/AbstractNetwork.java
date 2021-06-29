// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import rp.com.google.common.base.Predicate;
import java.util.Collections;
import rp.com.google.common.collect.ImmutableSet;
import rp.com.google.common.collect.Sets;
import rp.com.google.common.math.IntMath;
import rp.com.google.common.collect.Iterators;
import rp.com.google.common.base.Function;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import rp.com.google.common.annotations.Beta;

@Beta
public abstract class AbstractNetwork<N, E> implements Network<N, E>
{
    @Override
    public Graph<N> asGraph() {
        return new AbstractGraph<N>() {
            @Override
            public Set<N> nodes() {
                return AbstractNetwork.this.nodes();
            }
            
            @Override
            public Set<EndpointPair<N>> edges() {
                if (AbstractNetwork.this.allowsParallelEdges()) {
                    return (Set<EndpointPair<N>>)super.edges();
                }
                return new AbstractSet<EndpointPair<N>>() {
                    @Override
                    public Iterator<EndpointPair<N>> iterator() {
                        return Iterators.transform((Iterator<Object>)AbstractNetwork.this.edges().iterator(), (Function<? super Object, ? extends EndpointPair<N>>)new Function<E, EndpointPair<N>>() {
                            @Override
                            public EndpointPair<N> apply(final E edge) {
                                return AbstractNetwork.this.incidentNodes(edge);
                            }
                        });
                    }
                    
                    @Override
                    public int size() {
                        return AbstractNetwork.this.edges().size();
                    }
                    
                    @Override
                    public boolean contains(final Object obj) {
                        if (!(obj instanceof EndpointPair)) {
                            return false;
                        }
                        final EndpointPair<?> endpointPair = (EndpointPair<?>)obj;
                        return AbstractGraph.this.isDirected() == endpointPair.isOrdered() && AbstractGraph.this.nodes().contains(endpointPair.nodeU()) && AbstractGraph.this.successors(endpointPair.nodeU()).contains(endpointPair.nodeV());
                    }
                };
            }
            
            @Override
            public ElementOrder<N> nodeOrder() {
                return AbstractNetwork.this.nodeOrder();
            }
            
            @Override
            public boolean isDirected() {
                return AbstractNetwork.this.isDirected();
            }
            
            @Override
            public boolean allowsSelfLoops() {
                return AbstractNetwork.this.allowsSelfLoops();
            }
            
            @Override
            public Set<N> adjacentNodes(final N node) {
                return AbstractNetwork.this.adjacentNodes(node);
            }
            
            @Override
            public Set<N> predecessors(final N node) {
                return AbstractNetwork.this.predecessors(node);
            }
            
            @Override
            public Set<N> successors(final N node) {
                return AbstractNetwork.this.successors(node);
            }
        };
    }
    
    @Override
    public int degree(final N node) {
        if (this.isDirected()) {
            return IntMath.saturatedAdd(this.inEdges(node).size(), this.outEdges(node).size());
        }
        return IntMath.saturatedAdd(this.incidentEdges(node).size(), this.edgesConnecting(node, node).size());
    }
    
    @Override
    public int inDegree(final N node) {
        return this.isDirected() ? this.inEdges(node).size() : this.degree(node);
    }
    
    @Override
    public int outDegree(final N node) {
        return this.isDirected() ? this.outEdges(node).size() : this.degree(node);
    }
    
    @Override
    public Set<E> adjacentEdges(final E edge) {
        final EndpointPair<N> endpointPair = this.incidentNodes(edge);
        final Set<E> endpointPairIncidentEdges = (Set<E>)Sets.union(this.incidentEdges(endpointPair.nodeU()), this.incidentEdges(endpointPair.nodeV()));
        return Sets.difference(endpointPairIncidentEdges, ImmutableSet.of(edge));
    }
    
    @Override
    public Set<E> edgesConnecting(final N nodeU, final N nodeV) {
        final Set<E> outEdgesU = this.outEdges(nodeU);
        final Set<E> inEdgesV = this.inEdges(nodeV);
        return (outEdgesU.size() <= inEdgesV.size()) ? Collections.unmodifiableSet((Set<? extends E>)Sets.filter((Set<? extends T>)outEdgesU, this.connectedPredicate(nodeU, nodeV))) : Collections.unmodifiableSet((Set<? extends E>)Sets.filter((Set<? extends T>)inEdgesV, this.connectedPredicate(nodeV, nodeU)));
    }
    
    private Predicate<E> connectedPredicate(final N nodePresent, final N nodeToCheck) {
        return new Predicate<E>() {
            @Override
            public boolean apply(final E edge) {
                return AbstractNetwork.this.incidentNodes(edge).adjacentNode(nodePresent).equals(nodeToCheck);
            }
        };
    }
    
    @Override
    public Optional<E> edgeConnecting(final N nodeU, final N nodeV) {
        final Set<E> edgesConnecting = this.edgesConnecting(nodeU, nodeV);
        switch (edgesConnecting.size()) {
            case 0: {
                return Optional.empty();
            }
            case 1: {
                return Optional.of(edgesConnecting.iterator().next());
            }
            default: {
                throw new IllegalArgumentException(String.format("Cannot call edgeConnecting() when parallel edges exist between %s and %s. Consider calling edgesConnecting() instead.", nodeU, nodeV));
            }
        }
    }
    
    @Override
    public E edgeConnectingOrNull(final N nodeU, final N nodeV) {
        return this.edgeConnecting(nodeU, nodeV).orElse(null);
    }
    
    @Override
    public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
        return !this.edgesConnecting(nodeU, nodeV).isEmpty();
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Network)) {
            return false;
        }
        final Network<?, ?> other = (Network<?, ?>)obj;
        return this.isDirected() == other.isDirected() && this.nodes().equals(other.nodes()) && edgeIncidentNodesMap((Network<Object, Object>)this).equals(edgeIncidentNodesMap(other));
    }
    
    @Override
    public final int hashCode() {
        return edgeIncidentNodesMap((Network<Object, Object>)this).hashCode();
    }
    
    @Override
    public String toString() {
        return "isDirected: " + this.isDirected() + ", allowsParallelEdges: " + this.allowsParallelEdges() + ", allowsSelfLoops: " + this.allowsSelfLoops() + ", nodes: " + this.nodes() + ", edges: " + edgeIncidentNodesMap((Network<Object, Object>)this);
    }
    
    private static <N, E> Map<E, EndpointPair<N>> edgeIncidentNodesMap(final Network<N, E> network) {
        final Function<E, EndpointPair<N>> edgeToIncidentNodesFn = new Function<E, EndpointPair<N>>() {
            @Override
            public EndpointPair<N> apply(final E edge) {
                return network.incidentNodes(edge);
            }
        };
        return Maps.asMap(network.edges(), edgeToIncidentNodesFn);
    }
}
