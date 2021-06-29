// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.base.Preconditions;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

class ConfigurableValueGraph<N, V> extends AbstractValueGraph<N, V>
{
    private final boolean isDirected;
    private final boolean allowsSelfLoops;
    private final ElementOrder<N> nodeOrder;
    protected final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
    protected long edgeCount;
    
    ConfigurableValueGraph(final AbstractGraphBuilder<? super N> builder) {
        this((AbstractGraphBuilder<? super Object>)builder, builder.nodeOrder.createMap(builder.expectedNodeCount.or(10)), 0L);
    }
    
    ConfigurableValueGraph(final AbstractGraphBuilder<? super N> builder, final Map<N, GraphConnections<N, V>> nodeConnections, final long edgeCount) {
        this.isDirected = builder.directed;
        this.allowsSelfLoops = builder.allowsSelfLoops;
        this.nodeOrder = builder.nodeOrder.cast();
        this.nodeConnections = ((nodeConnections instanceof TreeMap) ? new MapRetrievalCache<N, GraphConnections<N, V>>(nodeConnections) : new MapIteratorCache<N, GraphConnections<N, V>>(nodeConnections));
        this.edgeCount = Graphs.checkNonNegative(edgeCount);
    }
    
    @Override
    public Set<N> nodes() {
        return this.nodeConnections.unmodifiableKeySet();
    }
    
    @Override
    public boolean isDirected() {
        return this.isDirected;
    }
    
    @Override
    public boolean allowsSelfLoops() {
        return this.allowsSelfLoops;
    }
    
    @Override
    public ElementOrder<N> nodeOrder() {
        return this.nodeOrder;
    }
    
    @Override
    public Set<N> adjacentNodes(final N node) {
        return this.checkedConnections(node).adjacentNodes();
    }
    
    @Override
    public Set<N> predecessors(final N node) {
        return this.checkedConnections(node).predecessors();
    }
    
    @Override
    public Set<N> successors(final N node) {
        return this.checkedConnections(node).successors();
    }
    
    @Override
    public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        final GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        return connectionsU != null && connectionsU.successors().contains(nodeV);
    }
    
    @Override
    public V edgeValueOrDefault(final N nodeU, final N nodeV, final V defaultValue) {
        Preconditions.checkNotNull(nodeU);
        Preconditions.checkNotNull(nodeV);
        final GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        final V value = (connectionsU == null) ? null : connectionsU.value(nodeV);
        return (value == null) ? defaultValue : value;
    }
    
    @Override
    protected long edgeCount() {
        return this.edgeCount;
    }
    
    protected final GraphConnections<N, V> checkedConnections(final N node) {
        final GraphConnections<N, V> connections = this.nodeConnections.get(node);
        if (connections == null) {
            Preconditions.checkNotNull(node);
            throw new IllegalArgumentException("Node " + node + " is not an element of this graph.");
        }
        return connections;
    }
    
    protected final boolean containsNode(final N node) {
        return this.nodeConnections.containsKey(node);
    }
}
