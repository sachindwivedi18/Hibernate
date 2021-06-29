// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.Map;
import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    static <K, V> Map<K, V> newHashMapWithExpectedSize(final int expectedSize) {
        return (Map<K, V>)Maps.newHashMapWithExpectedSize(expectedSize);
    }
    
    static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(final int expectedSize) {
        return (Map<K, V>)Maps.newLinkedHashMapWithExpectedSize(expectedSize);
    }
    
    static <E> Set<E> newHashSetWithExpectedSize(final int expectedSize) {
        return (Set<E>)Sets.newHashSetWithExpectedSize(expectedSize);
    }
    
    static <E> Set<E> newLinkedHashSetWithExpectedSize(final int expectedSize) {
        return (Set<E>)Sets.newLinkedHashSetWithExpectedSize(expectedSize);
    }
    
    static <K, V> Map<K, V> preservesInsertionOrderOnPutsMap() {
        return (Map<K, V>)Maps.newLinkedHashMap();
    }
    
    static <E> Set<E> preservesInsertionOrderOnAddsSet() {
        return (Set<E>)Sets.newLinkedHashSet();
    }
    
    static <T> T[] newArray(final T[] reference, final int length) {
        final Class<?> type = reference.getClass().getComponentType();
        final T[] result = (T[])Array.newInstance(type, length);
        return result;
    }
    
    static MapMaker tryWeakKeys(final MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }
    
    static int reduceIterationsIfGwt(final int iterations) {
        return iterations;
    }
    
    static int reduceExponentIfGwt(final int exponent) {
        return exponent;
    }
    
    private Platform() {
    }
}
