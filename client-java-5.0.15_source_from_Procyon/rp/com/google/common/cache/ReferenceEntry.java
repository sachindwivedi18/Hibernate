// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
interface ReferenceEntry<K, V>
{
    LocalCache.ValueReference<K, V> getValueReference();
    
    void setValueReference(final LocalCache.ValueReference<K, V> p0);
    
    ReferenceEntry<K, V> getNext();
    
    int getHash();
    
    K getKey();
    
    long getAccessTime();
    
    void setAccessTime(final long p0);
    
    ReferenceEntry<K, V> getNextInAccessQueue();
    
    void setNextInAccessQueue(final ReferenceEntry<K, V> p0);
    
    ReferenceEntry<K, V> getPreviousInAccessQueue();
    
    void setPreviousInAccessQueue(final ReferenceEntry<K, V> p0);
    
    long getWriteTime();
    
    void setWriteTime(final long p0);
    
    ReferenceEntry<K, V> getNextInWriteQueue();
    
    void setNextInWriteQueue(final ReferenceEntry<K, V> p0);
    
    ReferenceEntry<K, V> getPreviousInWriteQueue();
    
    void setPreviousInWriteQueue(final ReferenceEntry<K, V> p0);
}
