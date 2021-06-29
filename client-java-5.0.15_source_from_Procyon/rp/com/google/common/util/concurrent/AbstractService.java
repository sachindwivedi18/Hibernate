// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import rp.com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public abstract class AbstractService implements Service
{
    private static final ListenerCallQueue.Event<Listener> STARTING_EVENT;
    private static final ListenerCallQueue.Event<Listener> RUNNING_EVENT;
    private static final ListenerCallQueue.Event<Listener> STOPPING_FROM_STARTING_EVENT;
    private static final ListenerCallQueue.Event<Listener> STOPPING_FROM_RUNNING_EVENT;
    private static final ListenerCallQueue.Event<Listener> TERMINATED_FROM_NEW_EVENT;
    private static final ListenerCallQueue.Event<Listener> TERMINATED_FROM_RUNNING_EVENT;
    private static final ListenerCallQueue.Event<Listener> TERMINATED_FROM_STOPPING_EVENT;
    private final Monitor monitor;
    private final Monitor.Guard isStartable;
    private final Monitor.Guard isStoppable;
    private final Monitor.Guard hasReachedRunning;
    private final Monitor.Guard isStopped;
    private final ListenerCallQueue<Listener> listeners;
    private volatile StateSnapshot snapshot;
    
    private static ListenerCallQueue.Event<Listener> terminatedEvent(final State from) {
        return new ListenerCallQueue.Event<Listener>() {
            @Override
            public void call(final Listener listener) {
                listener.terminated(from);
            }
            
            @Override
            public String toString() {
                return "terminated({from = " + from + "})";
            }
        };
    }
    
    private static ListenerCallQueue.Event<Listener> stoppingEvent(final State from) {
        return new ListenerCallQueue.Event<Listener>() {
            @Override
            public void call(final Listener listener) {
                listener.stopping(from);
            }
            
            @Override
            public String toString() {
                return "stopping({from = " + from + "})";
            }
        };
    }
    
    protected AbstractService() {
        this.monitor = new Monitor();
        this.isStartable = new IsStartableGuard();
        this.isStoppable = new IsStoppableGuard();
        this.hasReachedRunning = new HasReachedRunningGuard();
        this.isStopped = new IsStoppedGuard();
        this.listeners = new ListenerCallQueue<Listener>();
        this.snapshot = new StateSnapshot(State.NEW);
    }
    
    @ForOverride
    protected abstract void doStart();
    
    @ForOverride
    protected abstract void doStop();
    
    @CanIgnoreReturnValue
    @Override
    public final Service startAsync() {
        if (this.monitor.enterIf(this.isStartable)) {
            try {
                this.snapshot = new StateSnapshot(State.STARTING);
                this.enqueueStartingEvent();
                this.doStart();
            }
            catch (Throwable startupFailure) {
                this.notifyFailed(startupFailure);
            }
            finally {
                this.monitor.leave();
                this.dispatchListenerEvents();
            }
            return this;
        }
        throw new IllegalStateException("Service " + this + " has already been started");
    }
    
    @CanIgnoreReturnValue
    @Override
    public final Service stopAsync() {
        if (this.monitor.enterIf(this.isStoppable)) {
            try {
                final State previous = this.state();
                switch (previous) {
                    case NEW: {
                        this.snapshot = new StateSnapshot(State.TERMINATED);
                        this.enqueueTerminatedEvent(State.NEW);
                        break;
                    }
                    case STARTING: {
                        this.snapshot = new StateSnapshot(State.STARTING, true, null);
                        this.enqueueStoppingEvent(State.STARTING);
                        break;
                    }
                    case RUNNING: {
                        this.snapshot = new StateSnapshot(State.STOPPING);
                        this.enqueueStoppingEvent(State.RUNNING);
                        this.doStop();
                        break;
                    }
                    case STOPPING:
                    case TERMINATED:
                    case FAILED: {
                        throw new AssertionError((Object)("isStoppable is incorrectly implemented, saw: " + previous));
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected state: " + previous));
                    }
                }
            }
            catch (Throwable shutdownFailure) {
                this.notifyFailed(shutdownFailure);
            }
            finally {
                this.monitor.leave();
                this.dispatchListenerEvents();
            }
        }
        return this;
    }
    
    @Override
    public final void awaitRunning() {
        this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);
        try {
            this.checkCurrentState(State.RUNNING);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitRunning(final long timeout, final TimeUnit unit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, timeout, unit)) {
            try {
                this.checkCurrentState(State.RUNNING);
            }
            finally {
                this.monitor.leave();
            }
            return;
        }
        throw new TimeoutException("Timed out waiting for " + this + " to reach the RUNNING state.");
    }
    
    @Override
    public final void awaitTerminated() {
        this.monitor.enterWhenUninterruptibly(this.isStopped);
        try {
            this.checkCurrentState(State.TERMINATED);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitTerminated(final long timeout, final TimeUnit unit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.isStopped, timeout, unit)) {
            try {
                this.checkCurrentState(State.TERMINATED);
            }
            finally {
                this.monitor.leave();
            }
            return;
        }
        throw new TimeoutException("Timed out waiting for " + this + " to reach a terminal state. Current state: " + this.state());
    }
    
    @GuardedBy("monitor")
    private void checkCurrentState(final State expected) {
        final State actual = this.state();
        if (actual == expected) {
            return;
        }
        if (actual == State.FAILED) {
            throw new IllegalStateException("Expected the service " + this + " to be " + expected + ", but the service has FAILED", this.failureCause());
        }
        throw new IllegalStateException("Expected the service " + this + " to be " + expected + ", but was " + actual);
    }
    
    protected final void notifyStarted() {
        this.monitor.enter();
        try {
            if (this.snapshot.state != State.STARTING) {
                final IllegalStateException failure = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);
                this.notifyFailed(failure);
                throw failure;
            }
            if (this.snapshot.shutdownWhenStartupFinishes) {
                this.snapshot = new StateSnapshot(State.STOPPING);
                this.doStop();
            }
            else {
                this.snapshot = new StateSnapshot(State.RUNNING);
                this.enqueueRunningEvent();
            }
        }
        finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
        }
    }
    
    protected final void notifyStopped() {
        this.monitor.enter();
        try {
            final State previous = this.snapshot.state;
            if (previous != State.STOPPING && previous != State.RUNNING) {
                final IllegalStateException failure = new IllegalStateException("Cannot notifyStopped() when the service is " + previous);
                this.notifyFailed(failure);
                throw failure;
            }
            this.snapshot = new StateSnapshot(State.TERMINATED);
            this.enqueueTerminatedEvent(previous);
        }
        finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
        }
    }
    
    protected final void notifyFailed(final Throwable cause) {
        Preconditions.checkNotNull(cause);
        this.monitor.enter();
        try {
            final State previous = this.state();
            switch (previous) {
                case NEW:
                case TERMINATED: {
                    throw new IllegalStateException("Failed while in state:" + previous, cause);
                }
                case STARTING:
                case RUNNING:
                case STOPPING: {
                    this.snapshot = new StateSnapshot(State.FAILED, false, cause);
                    this.enqueueFailedEvent(previous, cause);
                    break;
                }
                case FAILED: {
                    break;
                }
                default: {
                    throw new AssertionError((Object)("Unexpected state: " + previous));
                }
            }
        }
        finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
        }
    }
    
    @Override
    public final boolean isRunning() {
        return this.state() == State.RUNNING;
    }
    
    @Override
    public final State state() {
        return this.snapshot.externalState();
    }
    
    @Override
    public final Throwable failureCause() {
        return this.snapshot.failureCause();
    }
    
    @Override
    public final void addListener(final Listener listener, final Executor executor) {
        this.listeners.addListener(listener, executor);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.state() + "]";
    }
    
    private void dispatchListenerEvents() {
        if (!this.monitor.isOccupiedByCurrentThread()) {
            this.listeners.dispatch();
        }
    }
    
    private void enqueueStartingEvent() {
        this.listeners.enqueue(AbstractService.STARTING_EVENT);
    }
    
    private void enqueueRunningEvent() {
        this.listeners.enqueue(AbstractService.RUNNING_EVENT);
    }
    
    private void enqueueStoppingEvent(final State from) {
        if (from == State.STARTING) {
            this.listeners.enqueue(AbstractService.STOPPING_FROM_STARTING_EVENT);
        }
        else {
            if (from != State.RUNNING) {
                throw new AssertionError();
            }
            this.listeners.enqueue(AbstractService.STOPPING_FROM_RUNNING_EVENT);
        }
    }
    
    private void enqueueTerminatedEvent(final State from) {
        switch (from) {
            case NEW: {
                this.listeners.enqueue(AbstractService.TERMINATED_FROM_NEW_EVENT);
                break;
            }
            case RUNNING: {
                this.listeners.enqueue(AbstractService.TERMINATED_FROM_RUNNING_EVENT);
                break;
            }
            case STOPPING: {
                this.listeners.enqueue(AbstractService.TERMINATED_FROM_STOPPING_EVENT);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private void enqueueFailedEvent(final State from, final Throwable cause) {
        this.listeners.enqueue(new ListenerCallQueue.Event<Listener>() {
            @Override
            public void call(final Listener listener) {
                listener.failed(from, cause);
            }
            
            @Override
            public String toString() {
                return "failed({from = " + from + ", cause = " + cause + "})";
            }
        });
    }
    
    static {
        STARTING_EVENT = new ListenerCallQueue.Event<Listener>() {
            @Override
            public void call(final Listener listener) {
                listener.starting();
            }
            
            @Override
            public String toString() {
                return "starting()";
            }
        };
        RUNNING_EVENT = new ListenerCallQueue.Event<Listener>() {
            @Override
            public void call(final Listener listener) {
                listener.running();
            }
            
            @Override
            public String toString() {
                return "running()";
            }
        };
        STOPPING_FROM_STARTING_EVENT = stoppingEvent(State.STARTING);
        STOPPING_FROM_RUNNING_EVENT = stoppingEvent(State.RUNNING);
        TERMINATED_FROM_NEW_EVENT = terminatedEvent(State.NEW);
        TERMINATED_FROM_RUNNING_EVENT = terminatedEvent(State.RUNNING);
        TERMINATED_FROM_STOPPING_EVENT = terminatedEvent(State.STOPPING);
    }
    
    private final class IsStartableGuard extends Monitor.Guard
    {
        IsStartableGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state() == State.NEW;
        }
    }
    
    private final class IsStoppableGuard extends Monitor.Guard
    {
        IsStoppableGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state().compareTo(State.RUNNING) <= 0;
        }
    }
    
    private final class HasReachedRunningGuard extends Monitor.Guard
    {
        HasReachedRunningGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state().compareTo(State.RUNNING) >= 0;
        }
    }
    
    private final class IsStoppedGuard extends Monitor.Guard
    {
        IsStoppedGuard() {
            super(AbstractService.this.monitor);
        }
        
        @Override
        public boolean isSatisfied() {
            return AbstractService.this.state().isTerminal();
        }
    }
    
    private static final class StateSnapshot
    {
        final State state;
        final boolean shutdownWhenStartupFinishes;
        final Throwable failure;
        
        StateSnapshot(final State internalState) {
            this(internalState, false, null);
        }
        
        StateSnapshot(final State internalState, final boolean shutdownWhenStartupFinishes, final Throwable failure) {
            Preconditions.checkArgument(!shutdownWhenStartupFinishes || internalState == State.STARTING, "shutdownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", internalState);
            Preconditions.checkArgument(!(failure != null ^ internalState == State.FAILED), "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", internalState, failure);
            this.state = internalState;
            this.shutdownWhenStartupFinishes = shutdownWhenStartupFinishes;
            this.failure = failure;
        }
        
        State externalState() {
            if (this.shutdownWhenStartupFinishes && this.state == State.STARTING) {
                return State.STOPPING;
            }
            return this.state;
        }
        
        Throwable failureCause() {
            Preconditions.checkState(this.state == State.FAILED, "failureCause() is only valid if the service has failed, service is %s", this.state);
            return this.failure;
        }
    }
}
