// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
interface PatternCompiler
{
    CommonPattern compile(final String p0);
    
    boolean isPcreLike();
}
