// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import java.util.Collection;
import java.util.Deque;
import rp.com.google.common.collect.AbstractIterator;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.Queue;
import rp.com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import rp.com.google.common.collect.Iterables;
import rp.com.google.common.collect.ImmutableSet;
import rp.com.google.common.base.Preconditions;
import rp.com.google.common.annotations.Beta;

@Beta
public abstract class Traverser<N>
{
    public static <N> Traverser<N> forGraph(final SuccessorsFunction<N> graph) {
        Preconditions.checkNotNull(graph);
        return new GraphTraverser<N>(graph);
    }
    
    public static <N> Traverser<N> forTree(final SuccessorsFunction<N> tree) {
        Preconditions.checkNotNull(tree);
        if (tree instanceof BaseGraph) {
            Preconditions.checkArgument(((BaseGraph)tree).isDirected(), (Object)"Undirected graphs can never be trees.");
        }
        if (tree instanceof Network) {
            Preconditions.checkArgument(((Network)tree).isDirected(), (Object)"Undirected networks can never be trees.");
        }
        return new TreeTraverser<N>(tree);
    }
    
    public abstract Iterable<N> breadthFirst(final N p0);
    
    public abstract Iterable<N> breadthFirst(final Iterable<? extends N> p0);
    
    public abstract Iterable<N> depthFirstPreOrder(final N p0);
    
    public abstract Iterable<N> depthFirstPreOrder(final Iterable<? extends N> p0);
    
    public abstract Iterable<N> depthFirstPostOrder(final N p0);
    
    public abstract Iterable<N> depthFirstPostOrder(final Iterable<? extends N> p0);
    
    private Traverser() {
    }
    
    private static final class GraphTraverser<N> extends Traverser<N>
    {
        private final SuccessorsFunction<N> graph;
        
        GraphTraverser(final SuccessorsFunction<N> graph) {
            super(null);
            this.graph = Preconditions.checkNotNull(graph);
        }
        
        @Override
        public Iterable<N> breadthFirst(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.breadthFirst((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> breadthFirst(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N startNode : startNodes) {
                this.checkThatNodeIsInGraph(startNode);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new BreadthFirstIterator(startNodes);
                }
            };
        }
        
        @Override
        public Iterable<N> depthFirstPreOrder(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.depthFirstPreOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> depthFirstPreOrder(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N startNode : startNodes) {
                this.checkThatNodeIsInGraph(startNode);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new DepthFirstIterator(startNodes, Order.PREORDER);
                }
            };
        }
        
        @Override
        public Iterable<N> depthFirstPostOrder(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.depthFirstPostOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> depthFirstPostOrder(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N startNode : startNodes) {
                this.checkThatNodeIsInGraph(startNode);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new DepthFirstIterator(startNodes, Order.POSTORDER);
                }
            };
        }
        
        private void checkThatNodeIsInGraph(final N startNode) {
            this.graph.successors(startNode);
        }
        
        private final class BreadthFirstIterator extends UnmodifiableIterator<N>
        {
            private final Queue<N> queue;
            private final Set<N> visited;
            
            BreadthFirstIterator(final Iterable<? extends N> roots) {
                this.queue = new ArrayDeque<N>();
                this.visited = new HashSet<N>();
                for (final N root : roots) {
                    if (this.visited.add(root)) {
                        this.queue.add(root);
                    }
                }
            }
            
            @Override
            public boolean hasNext() {
                return !this.queue.isEmpty();
            }
            
            @Override
            public N next() {
                final N current = this.queue.remove();
                for (final N neighbor : GraphTraverser.this.graph.successors(current)) {
                    if (this.visited.add(neighbor)) {
                        this.queue.add(neighbor);
                    }
                }
                return current;
            }
        }
        
        private final class DepthFirstIterator extends AbstractIterator<N>
        {
            private final Deque<NodeAndSuccessors> stack;
            private final Set<N> visited;
            private final Order order;
            
            DepthFirstIterator(final Iterable<? extends N> roots, final Order order) {
                this.stack = new ArrayDeque<NodeAndSuccessors>();
                this.visited = new HashSet<N>();
                this.stack.push(new NodeAndSuccessors(null, roots));
                this.order = order;
            }
            
            @Override
            protected N computeNext() {
                while (!this.stack.isEmpty()) {
                    final NodeAndSuccessors nodeAndSuccessors = this.stack.getFirst();
                    final boolean firstVisit = this.visited.add(nodeAndSuccessors.node);
                    final boolean lastVisit = !nodeAndSuccessors.successorIterator.hasNext();
                    final boolean produceNode = (firstVisit && this.order == Order.PREORDER) || (lastVisit && this.order == Order.POSTORDER);
                    if (lastVisit) {
                        this.stack.pop();
                    }
                    else {
                        final N successor = (N)nodeAndSuccessors.successorIterator.next();
                        if (!this.visited.contains(successor)) {
                            this.stack.push(this.withSuccessors(successor));
                        }
                    }
                    if (produceNode && nodeAndSuccessors.node != null) {
                        return nodeAndSuccessors.node;
                    }
                }
                return this.endOfData();
            }
            
            NodeAndSuccessors withSuccessors(final N node) {
                return new NodeAndSuccessors(node, GraphTraverser.this.graph.successors(node));
            }
            
            private final class NodeAndSuccessors
            {
                final N node;
                final Iterator<? extends N> successorIterator;
                
                NodeAndSuccessors(final N node, final Iterable<? extends N> successors) {
                    this.node = node;
                    this.successorIterator = successors.iterator();
                }
            }
        }
    }
    
    private static final class TreeTraverser<N> extends Traverser<N>
    {
        private final SuccessorsFunction<N> tree;
        
        TreeTraverser(final SuccessorsFunction<N> tree) {
            super(null);
            this.tree = Preconditions.checkNotNull(tree);
        }
        
        @Override
        public Iterable<N> breadthFirst(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.breadthFirst((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> breadthFirst(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N startNode : startNodes) {
                this.checkThatNodeIsInTree(startNode);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new BreadthFirstIterator(startNodes);
                }
            };
        }
        
        @Override
        public Iterable<N> depthFirstPreOrder(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.depthFirstPreOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> depthFirstPreOrder(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N node : startNodes) {
                this.checkThatNodeIsInTree(node);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new DepthFirstPreOrderIterator(startNodes);
                }
            };
        }
        
        @Override
        public Iterable<N> depthFirstPostOrder(final N startNode) {
            Preconditions.checkNotNull(startNode);
            return this.depthFirstPostOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
        }
        
        @Override
        public Iterable<N> depthFirstPostOrder(final Iterable<? extends N> startNodes) {
            Preconditions.checkNotNull(startNodes);
            if (Iterables.isEmpty(startNodes)) {
                return (Iterable<N>)ImmutableSet.of();
            }
            for (final N startNode : startNodes) {
                this.checkThatNodeIsInTree(startNode);
            }
            return new Iterable<N>() {
                @Override
                public Iterator<N> iterator() {
                    return new DepthFirstPostOrderIterator(startNodes);
                }
            };
        }
        
        private void checkThatNodeIsInTree(final N startNode) {
            this.tree.successors(startNode);
        }
        
        private final class BreadthFirstIterator extends UnmodifiableIterator<N>
        {
            private final Queue<N> queue;
            
            BreadthFirstIterator(final Iterable<? extends N> roots) {
                this.queue = new ArrayDeque<N>();
                for (final N root : roots) {
                    this.queue.add(root);
                }
            }
            
            @Override
            public boolean hasNext() {
                return !this.queue.isEmpty();
            }
            
            @Override
            public N next() {
                final N current = this.queue.remove();
                Iterables.addAll(this.queue, TreeTraverser.this.tree.successors(current));
                return current;
            }
        }
        
        private final class DepthFirstPreOrderIterator extends UnmodifiableIterator<N>
        {
            private final Deque<Iterator<? extends N>> stack;
            
            DepthFirstPreOrderIterator(final Iterable<? extends N> roots) {
                (this.stack = new ArrayDeque<Iterator<? extends N>>()).addLast(roots.iterator());
            }
            
            @Override
            public boolean hasNext() {
                return !this.stack.isEmpty();
            }
            
            @Override
            public N next() {
                final Iterator<? extends N> iterator = this.stack.getLast();
                final N result = Preconditions.checkNotNull((N)iterator.next());
                if (!iterator.hasNext()) {
                    this.stack.removeLast();
                }
                final Iterator<? extends N> childIterator = TreeTraverser.this.tree.successors(result).iterator();
                if (childIterator.hasNext()) {
                    this.stack.addLast(childIterator);
                }
                return result;
            }
        }
        
        private final class DepthFirstPostOrderIterator extends AbstractIterator<N>
        {
            private final ArrayDeque<NodeAndChildren> stack;
            
            DepthFirstPostOrderIterator(final Iterable<? extends N> roots) {
                (this.stack = new ArrayDeque<NodeAndChildren>()).addLast(new NodeAndChildren(null, roots));
            }
            
            @Override
            protected N computeNext() {
                while (!this.stack.isEmpty()) {
                    final NodeAndChildren top = this.stack.getLast();
                    if (top.childIterator.hasNext()) {
                        final N child = (N)top.childIterator.next();
                        this.stack.addLast(this.withChildren(child));
                    }
                    else {
                        this.stack.removeLast();
                        if (top.node != null) {
                            return top.node;
                        }
                        continue;
                    }
                }
                return this.endOfData();
            }
            
            NodeAndChildren withChildren(final N node) {
                return new NodeAndChildren(node, TreeTraverser.this.tree.successors(node));
            }
            
            private final class NodeAndChildren
            {
                final N node;
                final Iterator<? extends N> childIterator;
                
                NodeAndChildren(final N node, final Iterable<? extends N> children) {
                    this.node = node;
                    this.childIterator = children.iterator();
                }
            }
        }
    }
    
    private enum Order
    {
        PREORDER, 
        POSTORDER;
    }
}
