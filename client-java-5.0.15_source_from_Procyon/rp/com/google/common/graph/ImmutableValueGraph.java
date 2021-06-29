// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import java.util.Set;
import rp.com.google.common.collect.Maps;
import rp.com.google.common.base.Function;
import java.util.Iterator;
import rp.com.google.common.collect.ImmutableMap;
import rp.com.google.common.base.Preconditions;
import java.util.Map;
import rp.com.google.common.annotations.Beta;
import com.google.errorprone.annotations.Immutable;

@Immutable(containerOf = { "N", "V" })
@Beta
public final class ImmutableValueGraph<N, V> extends ConfigurableValueGraph<N, V>
{
    private ImmutableValueGraph(final ValueGraph<N, V> graph) {
        super((AbstractGraphBuilder<? super N>)ValueGraphBuilder.from(graph), getNodeConnections(graph), graph.edges().size());
    }
    
    public static <N, V> ImmutableValueGraph<N, V> copyOf(final ValueGraph<N, V> graph) {
        return (graph instanceof ImmutableValueGraph) ? ((ImmutableValueGraph)graph) : new ImmutableValueGraph<N, V>((ValueGraph<N, V>)graph);
    }
    
    @Deprecated
    public static <N, V> ImmutableValueGraph<N, V> copyOf(final ImmutableValueGraph<N, V> graph) {
        return Preconditions.checkNotNull(graph);
    }
    
    @Override
    public ImmutableGraph<N> asGraph() {
        return new ImmutableGraph<N>((BaseGraph<N>)this);
    }
    
    private static <N, V> ImmutableMap<N, GraphConnections<N, V>> getNodeConnections(final ValueGraph<N, V> graph) {
        final ImmutableMap.Builder<N, GraphConnections<N, V>> nodeConnections = ImmutableMap.builder();
        for (final N node : graph.nodes()) {
            nodeConnections.put(node, connectionsOf(graph, node));
        }
        return nodeConnections.build();
    }
    
    private static <N, V> GraphConnections<N, V> connectionsOf(final ValueGraph<N, V> graph, final N node) {
        final Function<N, V> successorNodeToValueFn = new Function<N, V>() {
            @Override
            public V apply(final N successorNode) {
                return graph.edgeValueOrDefault(node, successorNode, null);
            }
        };
        return (GraphConnections<N, V>)(graph.isDirected() ? DirectedGraphConnections.ofImmutable(graph.predecessors(node), (Map<N, Object>)Maps.asMap((Set<N>)graph.successors(node), (Function<? super N, V>)successorNodeToValueFn)) : UndirectedGraphConnections.ofImmutable((Map<Object, Object>)Maps.asMap((Set<N>)graph.adjacentNodes(node), (Function<? super N, V>)successorNodeToValueFn)));
    }
}
