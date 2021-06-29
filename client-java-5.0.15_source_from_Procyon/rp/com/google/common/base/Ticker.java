// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Ticker
{
    private static final Ticker SYSTEM_TICKER;
    
    protected Ticker() {
    }
    
    @CanIgnoreReturnValue
    public abstract long read();
    
    public static Ticker systemTicker() {
        return Ticker.SYSTEM_TICKER;
    }
    
    static {
        SYSTEM_TICKER = new Ticker() {
            @Override
            public long read() {
                return Platform.systemNanoTime();
            }
        };
    }
}
