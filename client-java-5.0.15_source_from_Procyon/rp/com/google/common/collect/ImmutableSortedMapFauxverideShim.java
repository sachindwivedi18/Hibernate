// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.function.Function;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V>
{
    @Deprecated
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(final Function<? super T, ? extends K> keyFunction, final Function<? super T, ? extends V> valueFunction) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(final Function<? super T, ? extends K> keyFunction, final Function<? super T, ? extends V> valueFunction, final BinaryOperator<V> mergeFunction) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap.Builder<K, V> builderWithExpectedSize(final int expectedSize) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k1, final V v1) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k1, final V v1, final K k2, final V v2) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5) {
        throw new UnsupportedOperationException();
    }
}