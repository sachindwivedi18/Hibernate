// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Iterator;
import java.util.Comparator;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface SortedIterable<T> extends Iterable<T>
{
    Comparator<? super T> comparator();
    
    Iterator<T> iterator();
}
