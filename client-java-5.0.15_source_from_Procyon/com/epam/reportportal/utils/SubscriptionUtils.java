// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import org.slf4j.LoggerFactory;
import io.reactivex.MaybeEmitter;
import io.reactivex.Maybe;
import io.reactivex.CompletableObserver;
import org.reactivestreams.Subscription;
import io.reactivex.FlowableSubscriber;
import javax.annotation.Nonnull;
import io.reactivex.disposables.Disposable;
import io.reactivex.MaybeObserver;
import org.slf4j.Logger;

public class SubscriptionUtils
{
    private static final Logger LOGGER;
    
    public static <T> MaybeObserver<T> logMaybeResults(final String type) {
        return (MaybeObserver<T>)new MaybeObserver<T>() {
            public void onSubscribe(@Nonnull final Disposable d) {
            }
            
            public void onSuccess(@Nonnull final T Result) {
                SubscriptionUtils.LOGGER.debug("{} successfully completed", (Object)type);
            }
            
            public void onError(@Nonnull final Throwable e) {
                SubscriptionUtils.LOGGER.error("{} completed with error ", (Object)type, (Object)e);
            }
            
            public void onComplete() {
                SubscriptionUtils.LOGGER.debug("{} completed", (Object)type);
            }
        };
    }
    
    public static <T> FlowableSubscriber<T> logFlowableResults(final String type) {
        return (FlowableSubscriber<T>)new FlowableSubscriber<T>() {
            public void onSubscribe(@Nonnull final Subscription s) {
            }
            
            public void onNext(final T result) {
            }
            
            public void onError(final Throwable e) {
                SubscriptionUtils.LOGGER.error("{} completed with error ", (Object)type, (Object)e);
            }
            
            public void onComplete() {
                SubscriptionUtils.LOGGER.debug("{} completed", (Object)type);
            }
        };
    }
    
    public static CompletableObserver logCompletableResults(final String type) {
        return (CompletableObserver)new CompletableObserver() {
            public void onSubscribe(@Nonnull final Disposable d) {
            }
            
            public void onError(@Nonnull final Throwable e) {
                SubscriptionUtils.LOGGER.error("[{}] ReportPortal {} execution error", new Object[] { Thread.currentThread().getId(), type, e });
            }
            
            public void onComplete() {
                SubscriptionUtils.LOGGER.debug("{} completed", (Object)type);
            }
        };
    }
    
    public static <T> Maybe<T> createConstantMaybe(final T rs) {
        return (Maybe<T>)Maybe.create(emitter -> {
            emitter.onSuccess(rs);
            emitter.onComplete();
        });
    }
    
    public static <T> Maybe<T> createConstantMaybe(final Throwable exception) {
        return (Maybe<T>)Maybe.create(emitter -> {
            emitter.onError(exception);
            emitter.onComplete();
        });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SubscriptionUtils.class);
    }
}
