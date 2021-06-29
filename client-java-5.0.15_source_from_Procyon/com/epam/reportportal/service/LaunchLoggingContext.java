// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import com.epam.reportportal.utils.http.HttpRequestUtils;
import io.reactivex.Flowable;
import java.util.List;
import com.epam.reportportal.message.TypeAwareByteSource;
import rp.com.google.common.io.ByteSource;
import com.epam.reportportal.utils.files.ImageConverter;
import java.util.function.Function;
import io.reactivex.Completable;
import io.reactivex.FlowableSubscriber;
import com.epam.reportportal.utils.SubscriptionUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Scheduler;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import io.reactivex.Maybe;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.ConcurrentHashMap;

public class LaunchLoggingContext
{
    private static final int DEFAULT_BUFFER_SIZE = 10;
    static final String DEFAULT_LAUNCH_KEY = "default";
    static final ConcurrentHashMap<String, LaunchLoggingContext> loggingContextMap;
    private final PublishSubject<Maybe<SaveLogRQ>> emitter;
    private final Maybe<String> launchUuid;
    private final boolean convertImages;
    
    private LaunchLoggingContext(final Maybe<String> launchUuid, final ReportPortalClient client, final Scheduler scheduler, final int bufferSize, final boolean convertImages) {
        this.launchUuid = launchUuid;
        this.emitter = (PublishSubject<Maybe<SaveLogRQ>>)PublishSubject.create();
        this.convertImages = convertImages;
        this.emitter.toFlowable(BackpressureStrategy.BUFFER).flatMap(Maybe::toFlowable).buffer(bufferSize).flatMap(rqs -> client.log(HttpRequestUtils.buildLogMultiPartRequest(rqs)).toFlowable()).doOnError(Throwable::printStackTrace).observeOn(scheduler).subscribe((FlowableSubscriber)SubscriptionUtils.logFlowableResults("Launch logging context"));
    }
    
    static LaunchLoggingContext init(final Maybe<String> launchUuid, final ReportPortalClient client, final Scheduler scheduler) {
        return init(launchUuid, client, scheduler, 10, false);
    }
    
    static LaunchLoggingContext init(final Maybe<String> launchUuid, final ReportPortalClient client, final Scheduler scheduler, final int bufferSize, final boolean convertImages) {
        final LaunchLoggingContext context = new LaunchLoggingContext(launchUuid, client, scheduler, bufferSize, convertImages);
        LaunchLoggingContext.loggingContextMap.put("default", context);
        return context;
    }
    
    public static Completable complete() {
        final LaunchLoggingContext loggingContext = LaunchLoggingContext.loggingContextMap.get("default");
        if (null != loggingContext) {
            return loggingContext.completed();
        }
        return Maybe.empty().ignoreElement();
    }
    
    void emit(final Function<String, SaveLogRQ> logSupplier) {
        this.emitter.onNext((Object)this.launchUuid.map(input -> {
            final SaveLogRQ rq = logSupplier.apply(input);
            final SaveLogRQ.File file = rq.getFile();
            if (this.convertImages && null != file && ImageConverter.isImage(file.getContentType())) {
                final TypeAwareByteSource source = ImageConverter.convert(ByteSource.wrap(file.getContent()));
                file.setContent(source.read());
                file.setContentType(source.getMediaType());
            }
            return rq;
        }));
    }
    
    private Completable completed() {
        this.emitter.onComplete();
        return this.emitter.ignoreElements();
    }
    
    static {
        loggingContextMap = new ConcurrentHashMap<String, LaunchLoggingContext>();
    }
}
