// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.cache;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface LongAddable
{
    void increment();
    
    void add(final long p0);
    
    long sum();
}
