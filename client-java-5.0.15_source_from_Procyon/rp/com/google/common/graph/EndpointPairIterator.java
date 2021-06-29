// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.collect.Sets;
import java.util.Set;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import rp.com.google.common.collect.AbstractIterator;

abstract class EndpointPairIterator<N> extends AbstractIterator<EndpointPair<N>>
{
    private final BaseGraph<N> graph;
    private final Iterator<N> nodeIterator;
    protected N node;
    protected Iterator<N> successorIterator;
    
    static <N> EndpointPairIterator<N> of(final BaseGraph<N> graph) {
        return (EndpointPairIterator<N>)(graph.isDirected() ? new Directed<Object>((BaseGraph)graph) : new Undirected<Object>((BaseGraph)graph));
    }
    
    private EndpointPairIterator(final BaseGraph<N> graph) {
        this.node = null;
        this.successorIterator = (Iterator<N>)ImmutableSet.of().iterator();
        this.graph = graph;
        this.nodeIterator = graph.nodes().iterator();
    }
    
    protected final boolean advance() {
        Preconditions.checkState(!this.successorIterator.hasNext());
        if (!this.nodeIterator.hasNext()) {
            return false;
        }
        this.node = this.nodeIterator.next();
        this.successorIterator = this.graph.successors(this.node).iterator();
        return true;
    }
    
    private static final class Directed<N> extends EndpointPairIterator<N>
    {
        private Directed(final BaseGraph<N> graph) {
            super(graph, null);
        }
        
        @Override
        protected EndpointPair<N> computeNext() {
            while (!this.successorIterator.hasNext()) {
                if (!this.advance()) {
                    return (EndpointPair<N>)this.endOfData();
                }
            }
            return EndpointPair.ordered(this.node, this.successorIterator.next());
        }
    }
    
    private static final class Undirected<N> extends EndpointPairIterator<N>
    {
        private Set<N> visitedNodes;
        
        private Undirected(final BaseGraph<N> graph) {
            super(graph, null);
            this.visitedNodes = (Set<N>)Sets.newHashSetWithExpectedSize(graph.nodes().size());
        }
        
        @Override
        protected EndpointPair<N> computeNext() {
            while (true) {
                if (this.successorIterator.hasNext()) {
                    final N otherNode = this.successorIterator.next();
                    if (!this.visitedNodes.contains(otherNode)) {
                        return EndpointPair.unordered(this.node, otherNode);
                    }
                    continue;
                }
                else {
                    this.visitedNodes.add(this.node);
                    if (!this.advance()) {
                        this.visitedNodes = null;
                        return (EndpointPair<N>)this.endOfData();
                    }
                    continue;
                }
            }
        }
    }
}
