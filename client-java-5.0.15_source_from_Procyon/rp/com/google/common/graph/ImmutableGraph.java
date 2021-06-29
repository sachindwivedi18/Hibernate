// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import rp.com.google.common.base.Function;
import java.util.Set;
import rp.com.google.common.collect.Maps;
import rp.com.google.common.base.Functions;
import java.util.Iterator;
import rp.com.google.common.collect.ImmutableMap;
import rp.com.google.common.base.Preconditions;
import java.util.Map;
import rp.com.google.common.annotations.Beta;
import com.google.errorprone.annotations.Immutable;

@Immutable(containerOf = { "N" })
@Beta
public class ImmutableGraph<N> extends ForwardingGraph<N>
{
    private final BaseGraph<N> backingGraph;
    
    ImmutableGraph(final BaseGraph<N> backingGraph) {
        this.backingGraph = backingGraph;
    }
    
    public static <N> ImmutableGraph<N> copyOf(final Graph<N> graph) {
        return (graph instanceof ImmutableGraph) ? ((ImmutableGraph)graph) : new ImmutableGraph<N>((BaseGraph<N>)new ConfigurableValueGraph((AbstractGraphBuilder<? super Object>)GraphBuilder.from(graph), (Map<Object, GraphConnections<Object, Object>>)getNodeConnections(graph), graph.edges().size()));
    }
    
    @Deprecated
    public static <N> ImmutableGraph<N> copyOf(final ImmutableGraph<N> graph) {
        return Preconditions.checkNotNull(graph);
    }
    
    private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(final Graph<N> graph) {
        final ImmutableMap.Builder<N, GraphConnections<N, GraphConstants.Presence>> nodeConnections = ImmutableMap.builder();
        for (final N node : graph.nodes()) {
            nodeConnections.put(node, connectionsOf(graph, node));
        }
        return nodeConnections.build();
    }
    
    private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(final Graph<N> graph, final N node) {
        final Function<Object, GraphConstants.Presence> edgeValueFn = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
        return (GraphConnections<N, GraphConstants.Presence>)(graph.isDirected() ? DirectedGraphConnections.ofImmutable(graph.predecessors(node), (Map<N, Object>)Maps.asMap((Set<N>)graph.successors(node), (Function<? super N, V>)edgeValueFn)) : UndirectedGraphConnections.ofImmutable((Map<Object, Object>)Maps.asMap((Set<N>)graph.adjacentNodes(node), (Function<? super N, V>)edgeValueFn)));
    }
    
    @Override
    protected BaseGraph<N> delegate() {
        return this.backingGraph;
    }
}
