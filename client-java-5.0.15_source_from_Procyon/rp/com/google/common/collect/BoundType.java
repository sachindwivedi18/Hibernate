// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import rp.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType
{
    OPEN(false), 
    CLOSED(true);
    
    final boolean inclusive;
    
    private BoundType(final boolean inclusive) {
        this.inclusive = inclusive;
    }
    
    static BoundType forBoolean(final boolean inclusive) {
        return inclusive ? BoundType.CLOSED : BoundType.OPEN;
    }
    
    BoundType flip() {
        return forBoolean(!this.inclusive);
    }
}
