// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import java.util.Optional;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Queue;
import java.util.Collections;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import rp.com.google.common.base.Preconditions;
import java.util.Set;
import rp.com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.HashSet;
import rp.com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Map;
import rp.com.google.common.collect.Maps;
import rp.com.google.common.annotations.Beta;

@Beta
public final class Graphs
{
    private Graphs() {
    }
    
    public static <N> boolean hasCycle(final Graph<N> graph) {
        final int numEdges = graph.edges().size();
        if (numEdges == 0) {
            return false;
        }
        if (!graph.isDirected() && numEdges >= graph.nodes().size()) {
            return true;
        }
        final Map<Object, NodeVisitState> visitedNodes = (Map<Object, NodeVisitState>)Maps.newHashMapWithExpectedSize(graph.nodes().size());
        for (final N node : graph.nodes()) {
            if (subgraphHasCycle(graph, visitedNodes, node, (N)null)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasCycle(final Network<?, ?> network) {
        return (!network.isDirected() && network.allowsParallelEdges() && network.edges().size() > network.asGraph().edges().size()) || hasCycle(network.asGraph());
    }
    
    private static <N> boolean subgraphHasCycle(final Graph<N> graph, final Map<Object, NodeVisitState> visitedNodes, final N node, final N previousNode) {
        final NodeVisitState state = visitedNodes.get(node);
        if (state == NodeVisitState.COMPLETE) {
            return false;
        }
        if (state == NodeVisitState.PENDING) {
            return true;
        }
        visitedNodes.put(node, NodeVisitState.PENDING);
        for (final N nextNode : graph.successors(node)) {
            if (canTraverseWithoutReusingEdge(graph, nextNode, previousNode) && subgraphHasCycle((Graph<Object>)graph, visitedNodes, nextNode, node)) {
                return true;
            }
        }
        visitedNodes.put(node, NodeVisitState.COMPLETE);
        return false;
    }
    
    private static boolean canTraverseWithoutReusingEdge(final Graph<?> graph, final Object nextNode, final Object previousNode) {
        return graph.isDirected() || !Objects.equal(previousNode, nextNode);
    }
    
    public static <N> Graph<N> transitiveClosure(final Graph<N> graph) {
        final MutableGraph<N> transitiveClosure = GraphBuilder.from(graph).allowsSelfLoops(true).build();
        if (graph.isDirected()) {
            for (final N node : graph.nodes()) {
                for (final N reachableNode : reachableNodes(graph, node)) {
                    transitiveClosure.putEdge(node, reachableNode);
                }
            }
        }
        else {
            final Set<N> visitedNodes = new HashSet<N>();
            for (final N node2 : graph.nodes()) {
                if (!visitedNodes.contains(node2)) {
                    final Set<N> reachableNodes = reachableNodes(graph, node2);
                    visitedNodes.addAll((Collection<? extends N>)reachableNodes);
                    int pairwiseMatch = 1;
                    for (final N nodeU : reachableNodes) {
                        for (final N nodeV : Iterables.limit(reachableNodes, pairwiseMatch++)) {
                            transitiveClosure.putEdge(nodeU, nodeV);
                        }
                    }
                }
            }
        }
        return transitiveClosure;
    }
    
    public static <N> Set<N> reachableNodes(final Graph<N> graph, final N node) {
        Preconditions.checkArgument(graph.nodes().contains(node), "Node %s is not an element of this graph.", node);
        final Set<N> visitedNodes = new LinkedHashSet<N>();
        final Queue<N> queuedNodes = new ArrayDeque<N>();
        visitedNodes.add(node);
        queuedNodes.add(node);
        while (!queuedNodes.isEmpty()) {
            final N currentNode = queuedNodes.remove();
            for (final N successor : graph.successors(currentNode)) {
                if (visitedNodes.add(successor)) {
                    queuedNodes.add(successor);
                }
            }
        }
        return Collections.unmodifiableSet((Set<? extends N>)visitedNodes);
    }
    
    public static <N> Graph<N> transpose(final Graph<N> graph) {
        if (!graph.isDirected()) {
            return graph;
        }
        if (graph instanceof TransposedGraph) {
            return (Graph<N>)((TransposedGraph)graph).graph;
        }
        return new TransposedGraph<N>(graph);
    }
    
    public static <N, V> ValueGraph<N, V> transpose(final ValueGraph<N, V> graph) {
        if (!graph.isDirected()) {
            return graph;
        }
        if (graph instanceof TransposedValueGraph) {
            return (ValueGraph<N, V>)((TransposedValueGraph)graph).graph;
        }
        return new TransposedValueGraph<N, V>(graph);
    }
    
    public static <N, E> Network<N, E> transpose(final Network<N, E> network) {
        if (!network.isDirected()) {
            return network;
        }
        if (network instanceof TransposedNetwork) {
            return (Network<N, E>)((TransposedNetwork)network).network;
        }
        return new TransposedNetwork<N, E>(network);
    }
    
    public static <N> MutableGraph<N> inducedSubgraph(final Graph<N> graph, final Iterable<? extends N> nodes) {
        final MutableGraph<N> subgraph = (nodes instanceof Collection) ? GraphBuilder.from(graph).expectedNodeCount(((Collection)nodes).size()).build() : GraphBuilder.from(graph).build();
        for (final N node : nodes) {
            subgraph.addNode(node);
        }
        for (final N node : subgraph.nodes()) {
            for (final N successorNode : graph.successors(node)) {
                if (subgraph.nodes().contains(successorNode)) {
                    subgraph.putEdge(node, successorNode);
                }
            }
        }
        return subgraph;
    }
    
    public static <N, V> MutableValueGraph<N, V> inducedSubgraph(final ValueGraph<N, V> graph, final Iterable<? extends N> nodes) {
        final MutableValueGraph<N, V> subgraph = (nodes instanceof Collection) ? ValueGraphBuilder.from(graph).expectedNodeCount(((Collection)nodes).size()).build() : ValueGraphBuilder.from(graph).build();
        for (final N node : nodes) {
            subgraph.addNode(node);
        }
        for (final N node : subgraph.nodes()) {
            for (final N successorNode : graph.successors(node)) {
                if (subgraph.nodes().contains(successorNode)) {
                    subgraph.putEdgeValue(node, successorNode, graph.edgeValueOrDefault(node, successorNode, null));
                }
            }
        }
        return subgraph;
    }
    
    public static <N, E> MutableNetwork<N, E> inducedSubgraph(final Network<N, E> network, final Iterable<? extends N> nodes) {
        final MutableNetwork<N, E> subgraph = (nodes instanceof Collection) ? NetworkBuilder.from(network).expectedNodeCount(((Collection)nodes).size()).build() : NetworkBuilder.from(network).build();
        for (final N node : nodes) {
            subgraph.addNode(node);
        }
        for (final N node : subgraph.nodes()) {
            for (final E edge : network.outEdges(node)) {
                final N successorNode = network.incidentNodes(edge).adjacentNode(node);
                if (subgraph.nodes().contains(successorNode)) {
                    subgraph.addEdge(node, successorNode, edge);
                }
            }
        }
        return subgraph;
    }
    
    public static <N> MutableGraph<N> copyOf(final Graph<N> graph) {
        final MutableGraph<N> copy = GraphBuilder.from(graph).expectedNodeCount(graph.nodes().size()).build();
        for (final N node : graph.nodes()) {
            copy.addNode(node);
        }
        for (final EndpointPair<N> edge : graph.edges()) {
            copy.putEdge(edge.nodeU(), edge.nodeV());
        }
        return copy;
    }
    
    public static <N, V> MutableValueGraph<N, V> copyOf(final ValueGraph<N, V> graph) {
        final MutableValueGraph<N, V> copy = ValueGraphBuilder.from(graph).expectedNodeCount(graph.nodes().size()).build();
        for (final N node : graph.nodes()) {
            copy.addNode(node);
        }
        for (final EndpointPair<N> edge : graph.edges()) {
            copy.putEdgeValue(edge.nodeU(), edge.nodeV(), graph.edgeValueOrDefault(edge.nodeU(), edge.nodeV(), null));
        }
        return copy;
    }
    
    public static <N, E> MutableNetwork<N, E> copyOf(final Network<N, E> network) {
        final MutableNetwork<N, E> copy = NetworkBuilder.from(network).expectedNodeCount(network.nodes().size()).expectedEdgeCount(network.edges().size()).build();
        for (final N node : network.nodes()) {
            copy.addNode(node);
        }
        for (final E edge : network.edges()) {
            final EndpointPair<N> endpointPair = network.incidentNodes(edge);
            copy.addEdge(endpointPair.nodeU(), endpointPair.nodeV(), edge);
        }
        return copy;
    }
    
    @CanIgnoreReturnValue
    static int checkNonNegative(final int value) {
        Preconditions.checkArgument(value >= 0, "Not true that %s is non-negative.", value);
        return value;
    }
    
    @CanIgnoreReturnValue
    static long checkNonNegative(final long value) {
        Preconditions.checkArgument(value >= 0L, "Not true that %s is non-negative.", value);
        return value;
    }
    
    @CanIgnoreReturnValue
    static int checkPositive(final int value) {
        Preconditions.checkArgument(value > 0, "Not true that %s is positive.", value);
        return value;
    }
    
    @CanIgnoreReturnValue
    static long checkPositive(final long value) {
        Preconditions.checkArgument(value > 0L, "Not true that %s is positive.", value);
        return value;
    }
    
    private static class TransposedGraph<N> extends ForwardingGraph<N>
    {
        private final Graph<N> graph;
        
        TransposedGraph(final Graph<N> graph) {
            this.graph = graph;
        }
        
        @Override
        protected Graph<N> delegate() {
            return this.graph;
        }
        
        @Override
        public Set<N> predecessors(final N node) {
            return this.delegate().successors(node);
        }
        
        @Override
        public Set<N> successors(final N node) {
            return this.delegate().predecessors(node);
        }
        
        @Override
        public int inDegree(final N node) {
            return this.delegate().outDegree(node);
        }
        
        @Override
        public int outDegree(final N node) {
            return this.delegate().inDegree(node);
        }
        
        @Override
        public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
            return this.delegate().hasEdgeConnecting(nodeV, nodeU);
        }
    }
    
    private static class TransposedValueGraph<N, V> extends ForwardingValueGraph<N, V>
    {
        private final ValueGraph<N, V> graph;
        
        TransposedValueGraph(final ValueGraph<N, V> graph) {
            this.graph = graph;
        }
        
        @Override
        protected ValueGraph<N, V> delegate() {
            return this.graph;
        }
        
        @Override
        public Set<N> predecessors(final N node) {
            return this.delegate().successors(node);
        }
        
        @Override
        public Set<N> successors(final N node) {
            return this.delegate().predecessors(node);
        }
        
        @Override
        public int inDegree(final N node) {
            return this.delegate().outDegree(node);
        }
        
        @Override
        public int outDegree(final N node) {
            return this.delegate().inDegree(node);
        }
        
        @Override
        public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
            return this.delegate().hasEdgeConnecting(nodeV, nodeU);
        }
        
        @Override
        public Optional<V> edgeValue(final N nodeU, final N nodeV) {
            return this.delegate().edgeValue(nodeV, nodeU);
        }
        
        @Override
        public V edgeValueOrDefault(final N nodeU, final N nodeV, final V defaultValue) {
            return this.delegate().edgeValueOrDefault(nodeV, nodeU, defaultValue);
        }
    }
    
    private static class TransposedNetwork<N, E> extends ForwardingNetwork<N, E>
    {
        private final Network<N, E> network;
        
        TransposedNetwork(final Network<N, E> network) {
            this.network = network;
        }
        
        @Override
        protected Network<N, E> delegate() {
            return this.network;
        }
        
        @Override
        public Set<N> predecessors(final N node) {
            return this.delegate().successors(node);
        }
        
        @Override
        public Set<N> successors(final N node) {
            return this.delegate().predecessors(node);
        }
        
        @Override
        public int inDegree(final N node) {
            return this.delegate().outDegree(node);
        }
        
        @Override
        public int outDegree(final N node) {
            return this.delegate().inDegree(node);
        }
        
        @Override
        public Set<E> inEdges(final N node) {
            return this.delegate().outEdges(node);
        }
        
        @Override
        public Set<E> outEdges(final N node) {
            return this.delegate().inEdges(node);
        }
        
        @Override
        public EndpointPair<N> incidentNodes(final E edge) {
            final EndpointPair<N> endpointPair = this.delegate().incidentNodes(edge);
            return EndpointPair.of(this.network, endpointPair.nodeV(), endpointPair.nodeU());
        }
        
        @Override
        public Set<E> edgesConnecting(final N nodeU, final N nodeV) {
            return this.delegate().edgesConnecting(nodeV, nodeU);
        }
        
        @Override
        public Optional<E> edgeConnecting(final N nodeU, final N nodeV) {
            return this.delegate().edgeConnecting(nodeV, nodeU);
        }
        
        @Override
        public E edgeConnectingOrNull(final N nodeU, final N nodeV) {
            return this.delegate().edgeConnectingOrNull(nodeV, nodeU);
        }
        
        @Override
        public boolean hasEdgeConnecting(final N nodeU, final N nodeV) {
            return this.delegate().hasEdgeConnecting(nodeV, nodeU);
        }
    }
    
    private enum NodeVisitState
    {
        PENDING, 
        COMPLETE;
    }
}
