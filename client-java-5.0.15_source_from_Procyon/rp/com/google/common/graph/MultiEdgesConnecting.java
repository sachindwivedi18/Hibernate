// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.graph;

import java.util.Iterator;
import rp.com.google.common.collect.AbstractIterator;
import rp.com.google.common.collect.UnmodifiableIterator;
import rp.com.google.common.base.Preconditions;
import java.util.Map;
import java.util.AbstractSet;

abstract class MultiEdgesConnecting<E> extends AbstractSet<E>
{
    private final Map<E, ?> outEdgeToNode;
    private final Object targetNode;
    
    MultiEdgesConnecting(final Map<E, ?> outEdgeToNode, final Object targetNode) {
        this.outEdgeToNode = Preconditions.checkNotNull(outEdgeToNode);
        this.targetNode = Preconditions.checkNotNull(targetNode);
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        final Iterator<? extends Map.Entry<E, ?>> entries = this.outEdgeToNode.entrySet().iterator();
        return new AbstractIterator<E>() {
            @Override
            protected E computeNext() {
                while (entries.hasNext()) {
                    final Map.Entry<E, ?> entry = entries.next();
                    if (MultiEdgesConnecting.this.targetNode.equals(entry.getValue())) {
                        return entry.getKey();
                    }
                }
                return this.endOfData();
            }
        };
    }
    
    @Override
    public boolean contains(final Object edge) {
        return this.targetNode.equals(this.outEdgeToNode.get(edge));
    }
}
