// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import javax.annotation.Nonnull;
import io.reactivex.functions.Predicate;

public class RetryWithDelay implements Predicate<Throwable>
{
    private final Predicate<? super Throwable> predicate;
    private final long maxRetries;
    private final long retryDelayMillis;
    private int retryCount;
    
    public RetryWithDelay(final Predicate<? super Throwable> predicate, final long maxRetries, final long retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
        this.predicate = predicate;
    }
    
    public boolean test(@Nonnull final Throwable throwable) throws Exception {
        try {
            if (!this.predicate.test((Object)throwable)) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        if (++this.retryCount < this.maxRetries) {
            Thread.sleep(this.retryDelayMillis);
            return true;
        }
        return false;
    }
}
