// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.logging.Level;
import java.util.concurrent.RejectedExecutionException;
import rp.com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.Deque;
import java.util.logging.Logger;
import rp.com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.Executor;

@GwtIncompatible
final class SequentialExecutor implements Executor
{
    private static final Logger log;
    private final Executor executor;
    @GuardedBy("queue")
    private final Deque<Runnable> queue;
    @GuardedBy("queue")
    private WorkerRunningState workerRunningState;
    @GuardedBy("queue")
    private long workerRunCount;
    private final QueueWorker worker;
    
    SequentialExecutor(final Executor executor) {
        this.queue = new ArrayDeque<Runnable>();
        this.workerRunningState = WorkerRunningState.IDLE;
        this.workerRunCount = 0L;
        this.worker = new QueueWorker();
        this.executor = Preconditions.checkNotNull(executor);
    }
    
    @Override
    public void execute(final Runnable task) {
        Preconditions.checkNotNull(task);
        final long oldRunCount;
        final Runnable submittedTask;
        synchronized (this.queue) {
            if (this.workerRunningState == WorkerRunningState.RUNNING || this.workerRunningState == WorkerRunningState.QUEUED) {
                this.queue.add(task);
                return;
            }
            oldRunCount = this.workerRunCount;
            submittedTask = new Runnable() {
                @Override
                public void run() {
                    task.run();
                }
            };
            this.queue.add(submittedTask);
            this.workerRunningState = WorkerRunningState.QUEUING;
        }
        try {
            this.executor.execute(this.worker);
        }
        catch (RuntimeException | Error ex) {
            final Throwable t2;
            final Throwable t = t2;
            synchronized (this.queue) {
                final boolean removed = (this.workerRunningState == WorkerRunningState.IDLE || this.workerRunningState == WorkerRunningState.QUEUING) && this.queue.removeLastOccurrence(submittedTask);
                if (!(t instanceof RejectedExecutionException) || removed) {
                    throw t;
                }
            }
            return;
        }
        final boolean alreadyMarkedQueued = this.workerRunningState != WorkerRunningState.QUEUING;
        if (alreadyMarkedQueued) {
            return;
        }
        synchronized (this.queue) {
            if (this.workerRunCount == oldRunCount && this.workerRunningState == WorkerRunningState.QUEUING) {
                this.workerRunningState = WorkerRunningState.QUEUED;
            }
        }
    }
    
    static {
        log = Logger.getLogger(SequentialExecutor.class.getName());
    }
    
    enum WorkerRunningState
    {
        IDLE, 
        QUEUING, 
        QUEUED, 
        RUNNING;
    }
    
    private final class QueueWorker implements Runnable
    {
        @Override
        public void run() {
            try {
                this.workOnQueue();
            }
            catch (Error e) {
                synchronized (SequentialExecutor.this.queue) {
                    SequentialExecutor.this.workerRunningState = WorkerRunningState.IDLE;
                }
                throw e;
            }
        }
        
        private void workOnQueue() {
            boolean interruptedDuringTask = false;
            boolean hasSetRunning = false;
            try {
                while (true) {
                    final Runnable task;
                    synchronized (SequentialExecutor.this.queue) {
                        if (!hasSetRunning) {
                            if (SequentialExecutor.this.workerRunningState == WorkerRunningState.RUNNING) {
                                return;
                            }
                            SequentialExecutor.this.workerRunCount++;
                            SequentialExecutor.this.workerRunningState = WorkerRunningState.RUNNING;
                            hasSetRunning = true;
                        }
                        task = SequentialExecutor.this.queue.poll();
                        if (task == null) {
                            SequentialExecutor.this.workerRunningState = WorkerRunningState.IDLE;
                            return;
                        }
                    }
                    interruptedDuringTask |= Thread.interrupted();
                    try {
                        task.run();
                    }
                    catch (RuntimeException e) {
                        SequentialExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + task, e);
                    }
                }
            }
            finally {
                if (interruptedDuringTask) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
