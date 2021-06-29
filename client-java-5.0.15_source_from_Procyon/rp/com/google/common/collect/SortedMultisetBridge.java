// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Set;
import java.util.SortedSet;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
interface SortedMultisetBridge<E> extends Multiset<E>
{
    SortedSet<E> elementSet();
}
