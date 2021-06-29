// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.aspect;

import java.util.function.Supplier;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.annotation.Nonnull;
import org.aspectj.lang.annotation.AfterThrowing;
import com.epam.reportportal.service.ReportPortal;
import rp.com.google.common.base.Throwables;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import org.aspectj.lang.annotation.AfterReturning;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import java.util.Calendar;
import com.epam.reportportal.listeners.ItemStatus;
import org.aspectj.lang.annotation.Before;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.reportportal.service.Launch;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import com.epam.reportportal.annotations.Step;
import io.reactivex.Maybe;
import java.util.Deque;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class StepAspect
{
    private static final ThreadLocal<Deque<Maybe<String>>> stepStack;
    
    @Pointcut("@annotation(step)")
    public void withStepAnnotation(final Step step) {
    }
    
    @Pointcut("execution(* *.*(..))")
    public void anyMethod() {
    }
    
    @Before(value = "anyMethod() && withStepAnnotation(step)", argNames = "joinPoint,step")
    public void startNestedStep(final JoinPoint joinPoint, final Step step) {
        if (!step.isIgnored()) {
            final MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            final Deque<Maybe<String>> steps = StepAspect.stepStack.get();
            final Maybe<String> parent = steps.peek();
            if (parent == null) {
                return;
            }
            final StartTestItemRQ startStepRequest = StepRequestUtils.buildStartStepRequest(signature, step, joinPoint);
            Launch.currentLaunch().startTestItem(parent, startStepRequest);
        }
    }
    
    @AfterReturning(value = "anyMethod() && withStepAnnotation(step)", argNames = "step")
    public void finishNestedStep(final Step step) {
        if (!step.isIgnored()) {
            final Deque<Maybe<String>> steps = StepAspect.stepStack.get();
            final Maybe<String> stepId = steps.peek();
            if (stepId == null) {
                return;
            }
            final FinishTestItemRQ finishStepRequest = StepRequestUtils.buildFinishStepRequest(ItemStatus.PASSED, Calendar.getInstance().getTime());
            Launch.currentLaunch().finishTestItem(stepId, finishStepRequest);
        }
    }
    
    @AfterThrowing(value = "anyMethod() && withStepAnnotation(step)", throwing = "throwable", argNames = "step,throwable")
    public void failedNestedStep(final Step step, final Throwable throwable) {
        if (!step.isIgnored()) {
            final Deque<Maybe<String>> steps = StepAspect.stepStack.get();
            final Maybe<String> stepId = steps.peek();
            if (stepId == null) {
                return;
            }
            final SaveLogRQ rq;
            ReportPortal.emitLog(itemUuid -> {
                rq = new SaveLogRQ();
                rq.setItemUuid(itemUuid);
                rq.setLevel("ERROR");
                rq.setLogTime(Calendar.getInstance().getTime());
                if (throwable != null) {
                    rq.setMessage(Throwables.getStackTraceAsString(throwable));
                }
                else {
                    rq.setMessage("Test has failed without exception");
                }
                rq.setLogTime(Calendar.getInstance().getTime());
                return rq;
            });
            final FinishTestItemRQ finishStepRequest = StepRequestUtils.buildFinishStepRequest(ItemStatus.FAILED, Calendar.getInstance().getTime());
            Launch.currentLaunch().finishTestItem(stepId, finishStepRequest);
        }
    }
    
    public static void setParentId(@Nonnull final Maybe<String> parent) {
        StepAspect.stepStack.get().push(parent);
    }
    
    public static void removeParentId(@Nonnull final Maybe<String> parentUuid) {
        StepAspect.stepStack.get().removeLastOccurrence(parentUuid);
    }
    
    static {
        stepStack = ThreadLocal.withInitial((Supplier<? extends Deque<Maybe<String>>>)ConcurrentLinkedDeque::new);
    }
}
