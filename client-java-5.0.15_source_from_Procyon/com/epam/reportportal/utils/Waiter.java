// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import java.util.function.Supplier;
import org.slf4j.LoggerFactory;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Callable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;

public class Waiter
{
    private static final Logger LOGGER;
    private static final ThreadLocal<Random> RANDOM;
    private final String waitDescription;
    private long durationNs;
    private long pollingNs;
    private final List<Class<? extends Throwable>> ignoreExceptions;
    private boolean useDiscrepancy;
    private float discrepancy;
    private double maxDiscrepancyNs;
    private boolean failOnTimeout;
    
    public Waiter(final String description) {
        this.durationNs = TimeUnit.MINUTES.toNanos(1L);
        this.pollingNs = TimeUnit.MILLISECONDS.toNanos(100L);
        this.ignoreExceptions = new ArrayList<Class<? extends Throwable>>();
        this.useDiscrepancy = false;
        this.discrepancy = 0.0f;
        this.maxDiscrepancyNs = this.pollingNs * this.discrepancy;
        this.failOnTimeout = false;
        this.waitDescription = description;
    }
    
    public Waiter duration(final long duration, final TimeUnit timeUnit) {
        assert duration >= 0L;
        this.durationNs = timeUnit.toNanos(duration);
        return this;
    }
    
    public Waiter pollingEvery(final long duration, final TimeUnit timeUnit) {
        assert duration > 0L;
        this.pollingNs = timeUnit.toNanos(duration);
        return this.useDiscrepancy ? this.applyRandomDiscrepancy(this.discrepancy) : this;
    }
    
    public Waiter ignore(final Class<? extends Throwable> exception) {
        assert exception != null;
        this.ignoreExceptions.add(exception);
        return this;
    }
    
    public Waiter applyRandomDiscrepancy(final float maximumDiscrepancy) {
        assert maximumDiscrepancy <= 1.0f && maximumDiscrepancy >= 0.0f;
        this.discrepancy = maximumDiscrepancy;
        this.useDiscrepancy = (maximumDiscrepancy > 0.0f);
        this.maxDiscrepancyNs = this.pollingNs * this.discrepancy;
        return this;
    }
    
    public Waiter timeoutFail() {
        this.failOnTimeout = true;
        return this;
    }
    
    private long getDiscrepancy() {
        if (!this.useDiscrepancy) {
            return 0L;
        }
        final Random random = Waiter.RANDOM.get();
        final double absoluteDiscrepancy = this.maxDiscrepancyNs * random.nextDouble();
        final double discrepancy = random.nextBoolean() ? absoluteDiscrepancy : (-absoluteDiscrepancy);
        return (long)discrepancy;
    }
    
    private boolean knownException(final Exception e) {
        for (final Class<? extends Throwable> known : this.ignoreExceptions) {
            if (known.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public <T> T till(final Callable<T> waitFor) {
        final long triesLong = this.durationNs / this.pollingNs;
        final int tries = (triesLong > 2147483647L) ? Integer.MAX_VALUE : ((int)triesLong);
        final CountDownLatch countDown = new CountDownLatch(tries);
        try {
            do {
                try {
                    final T result = waitFor.call();
                    if (result != null) {
                        return result;
                    }
                }
                catch (Exception e) {
                    if (!this.knownException(e)) {
                        Waiter.LOGGER.error("An exception caught while waiting for a result: " + e.getLocalizedMessage(), (Throwable)e);
                        throw new InternalReportPortalClientException(e.getLocalizedMessage(), e);
                    }
                    Waiter.LOGGER.trace("A known exception caught while waiting for a result: " + e.getLocalizedMessage(), (Throwable)e);
                }
                countDown.countDown();
            } while (!countDown.await(this.pollingNs + this.getDiscrepancy(), TimeUnit.NANOSECONDS));
            if (this.failOnTimeout) {
                throw new InternalReportPortalClientException(this.waitDescription + " timed out");
            }
        }
        catch (InterruptedException ignored) {
            Waiter.LOGGER.warn(this.waitDescription + " was interrupted");
        }
        return null;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Waiter.class);
        RANDOM = ThreadLocal.withInitial((Supplier<? extends Random>)Random::new);
    }
}
