// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Map;
import rp.com.google.common.base.Predicate;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface FilteredMultimap<K, V> extends Multimap<K, V>
{
    Multimap<K, V> unfiltered();
    
    Predicate<? super Map.Entry<K, V>> entryPredicate();
}
