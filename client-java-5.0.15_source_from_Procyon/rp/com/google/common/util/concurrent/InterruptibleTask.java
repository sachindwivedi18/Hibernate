// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import com.google.j2objc.annotations.ReflectionSupport;
import rp.com.google.common.annotations.GwtCompatible;
import java.util.concurrent.atomic.AtomicReference;

@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class InterruptibleTask<T> extends AtomicReference<Runnable> implements Runnable
{
    private static final Runnable DONE;
    private static final Runnable INTERRUPTING;
    
    @Override
    public final void run() {
        final Thread currentThread = Thread.currentThread();
        if (!this.compareAndSet(null, currentThread)) {
            return;
        }
        final boolean run = !this.isDone();
        T result = null;
        Throwable error = null;
        try {
            if (run) {
                result = this.runInterruptibly();
            }
        }
        catch (Throwable t) {
            error = t;
        }
        finally {
            if (!this.compareAndSet(currentThread, InterruptibleTask.DONE)) {
                while (this.get() == InterruptibleTask.INTERRUPTING) {
                    Thread.yield();
                }
            }
            if (run) {
                this.afterRanInterruptibly(result, error);
            }
        }
    }
    
    abstract boolean isDone();
    
    abstract T runInterruptibly() throws Exception;
    
    abstract void afterRanInterruptibly(final T p0, final Throwable p1);
    
    final void interruptTask() {
        final Runnable currentRunner = this.get();
        if (currentRunner instanceof Thread && this.compareAndSet(currentRunner, InterruptibleTask.INTERRUPTING)) {
            ((Thread)currentRunner).interrupt();
            this.set(InterruptibleTask.DONE);
        }
    }
    
    @Override
    public final String toString() {
        final Runnable state = this.get();
        String result;
        if (state == InterruptibleTask.DONE) {
            result = "running=[DONE]";
        }
        else if (state == InterruptibleTask.INTERRUPTING) {
            result = "running=[INTERRUPTED]";
        }
        else if (state instanceof Thread) {
            result = "running=[RUNNING ON " + ((Thread)state).getName() + "]";
        }
        else {
            result = "running=[NOT STARTED YET]";
        }
        return result + ", " + this.toPendingString();
    }
    
    abstract String toPendingString();
    
    static {
        DONE = new DoNothingRunnable();
        INTERRUPTING = new DoNothingRunnable();
    }
    
    private static final class DoNothingRunnable implements Runnable
    {
        @Override
        public void run() {
        }
    }
}
