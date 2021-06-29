// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import org.slf4j.LoggerFactory;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import io.reactivex.Maybe;
import com.epam.reportportal.service.step.DefaultStepReporter;
import com.epam.reportportal.service.step.StepReporter;
import com.epam.reportportal.listeners.ListenerParameters;
import org.slf4j.Logger;

public abstract class Launch
{
    private static final ThreadLocal<Launch> CURRENT_LAUNCH;
    static final Logger LOGGER;
    private final ListenerParameters parameters;
    private final StepReporter stepReporter;
    public static final Launch NOOP_LAUNCH;
    
    Launch(final ListenerParameters parameters, final StepReporter reporter) {
        this.parameters = parameters;
        this.stepReporter = reporter;
        Launch.CURRENT_LAUNCH.set(this);
    }
    
    Launch(final ListenerParameters parameters) {
        this.parameters = parameters;
        this.stepReporter = new DefaultStepReporter(this);
        Launch.CURRENT_LAUNCH.set(this);
    }
    
    public abstract Maybe<String> start();
    
    public abstract void finish(final FinishExecutionRQ p0);
    
    public abstract Maybe<String> startTestItem(final StartTestItemRQ p0);
    
    public abstract Maybe<String> startTestItem(final Maybe<String> p0, final StartTestItemRQ p1);
    
    public abstract Maybe<String> startTestItem(final Maybe<String> p0, final Maybe<String> p1, final StartTestItemRQ p2);
    
    public abstract Maybe<OperationCompletionRS> finishTestItem(final Maybe<String> p0, final FinishTestItemRQ p1);
    
    public ListenerParameters getParameters() {
        Launch.CURRENT_LAUNCH.set(this);
        return this.parameters;
    }
    
    public static Launch currentLaunch() {
        return Launch.CURRENT_LAUNCH.get();
    }
    
    public StepReporter getStepReporter() {
        return this.stepReporter;
    }
    
    static {
        CURRENT_LAUNCH = new InheritableThreadLocal<Launch>();
        LOGGER = LoggerFactory.getLogger((Class)Launch.class);
        NOOP_LAUNCH = new Launch(new ListenerParameters(), StepReporter.NOOP_STEP_REPORTER) {
            @Override
            public Maybe<String> start() {
                return (Maybe<String>)Maybe.empty();
            }
            
            @Override
            public void finish(final FinishExecutionRQ rq) {
            }
            
            @Override
            public Maybe<String> startTestItem(final StartTestItemRQ rq) {
                return (Maybe<String>)Maybe.empty();
            }
            
            @Override
            public Maybe<String> startTestItem(final Maybe<String> parentId, final StartTestItemRQ rq) {
                return (Maybe<String>)Maybe.empty();
            }
            
            @Override
            public Maybe<String> startTestItem(final Maybe<String> parentId, final Maybe<String> retryOf, final StartTestItemRQ rq) {
                return (Maybe<String>)Maybe.empty();
            }
            
            @Override
            public Maybe<OperationCompletionRS> finishTestItem(final Maybe<String> itemId, final FinishTestItemRQ rq) {
                return (Maybe<OperationCompletionRS>)Maybe.empty();
            }
        };
    }
}
