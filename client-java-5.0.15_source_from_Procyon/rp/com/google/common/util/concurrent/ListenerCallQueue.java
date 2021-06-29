// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.logging.Level;
import rp.com.google.common.collect.Queues;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.Queue;
import java.util.Iterator;
import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import rp.com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
final class ListenerCallQueue<L>
{
    private static final Logger logger;
    private final List<PerListenerQueue<L>> listeners;
    
    ListenerCallQueue() {
        this.listeners = Collections.synchronizedList(new ArrayList<PerListenerQueue<L>>());
    }
    
    public void addListener(final L listener, final Executor executor) {
        Preconditions.checkNotNull(listener, (Object)"listener");
        Preconditions.checkNotNull(executor, (Object)"executor");
        this.listeners.add(new PerListenerQueue<L>(listener, executor));
    }
    
    public void enqueue(final Event<L> event) {
        this.enqueueHelper(event, event);
    }
    
    public void enqueue(final Event<L> event, final String label) {
        this.enqueueHelper(event, label);
    }
    
    private void enqueueHelper(final Event<L> event, final Object label) {
        Preconditions.checkNotNull(event, (Object)"event");
        Preconditions.checkNotNull(label, (Object)"label");
        synchronized (this.listeners) {
            for (final PerListenerQueue<L> queue : this.listeners) {
                queue.add(event, label);
            }
        }
    }
    
    public void dispatch() {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).dispatch();
        }
    }
    
    static {
        logger = Logger.getLogger(ListenerCallQueue.class.getName());
    }
    
    private static final class PerListenerQueue<L> implements Runnable
    {
        final L listener;
        final Executor executor;
        @GuardedBy("this")
        final Queue<Event<L>> waitQueue;
        @GuardedBy("this")
        final Queue<Object> labelQueue;
        @GuardedBy("this")
        boolean isThreadScheduled;
        
        PerListenerQueue(final L listener, final Executor executor) {
            this.waitQueue = (Queue<Event<L>>)Queues.newArrayDeque();
            this.labelQueue = Queues.newArrayDeque();
            this.listener = Preconditions.checkNotNull(listener);
            this.executor = Preconditions.checkNotNull(executor);
        }
        
        synchronized void add(final Event<L> event, final Object label) {
            this.waitQueue.add(event);
            this.labelQueue.add(label);
        }
        
        void dispatch() {
            boolean scheduleEventRunner = false;
            synchronized (this) {
                if (!this.isThreadScheduled) {
                    this.isThreadScheduled = true;
                    scheduleEventRunner = true;
                }
            }
            if (scheduleEventRunner) {
                try {
                    this.executor.execute(this);
                }
                catch (RuntimeException e) {
                    synchronized (this) {
                        this.isThreadScheduled = false;
                    }
                    ListenerCallQueue.logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, e);
                    throw e;
                }
            }
        }
        
        @Override
        public void run() {
            boolean stillRunning = true;
            try {
                while (true) {
                    final Event<L> nextToRun;
                    final Object nextLabel;
                    synchronized (this) {
                        Preconditions.checkState(this.isThreadScheduled);
                        nextToRun = this.waitQueue.poll();
                        nextLabel = this.labelQueue.poll();
                        if (nextToRun == null) {
                            this.isThreadScheduled = false;
                            stillRunning = false;
                            break;
                        }
                    }
                    try {
                        nextToRun.call(this.listener);
                    }
                    catch (RuntimeException e) {
                        ListenerCallQueue.logger.log(Level.SEVERE, "Exception while executing callback: " + this.listener + " " + nextLabel, e);
                    }
                }
            }
            finally {
                if (stillRunning) {
                    synchronized (this) {
                        this.isThreadScheduled = false;
                    }
                }
            }
        }
    }
    
    interface Event<L>
    {
        void call(final L p0);
    }
}
