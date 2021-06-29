// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import java.util.function.Supplier;
import java.util.ArrayDeque;
import com.epam.reportportal.utils.http.HttpRequestUtils;
import io.reactivex.Flowable;
import java.util.List;
import io.reactivex.MaybeSource;
import java.io.IOException;
import com.epam.reportportal.message.TypeAwareByteSource;
import rp.com.google.common.io.ByteSource;
import com.epam.reportportal.utils.files.ImageConverter;
import java.util.function.Function;
import io.reactivex.FlowableSubscriber;
import com.epam.reportportal.utils.SubscriptionUtils;
import io.reactivex.functions.Consumer;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import io.reactivex.Maybe;
import io.reactivex.subjects.PublishSubject;
import java.util.Deque;

public class LoggingContext
{
    public static final int DEFAULT_BUFFER_SIZE = 10;
    static final ThreadLocal<Deque<LoggingContext>> CONTEXT_THREAD_LOCAL;
    private final PublishSubject<Maybe<SaveLogRQ>> emitter;
    private final Maybe<String> launchUuid;
    private final Maybe<String> itemUuid;
    private final boolean convertImages;
    
    public static LoggingContext init(final Maybe<String> launchUuid, final Maybe<String> itemUuid, final ReportPortalClient client, final Scheduler scheduler) {
        return init(launchUuid, itemUuid, client, scheduler, 10, false);
    }
    
    public static LoggingContext init(final Maybe<String> launchUuid, final Maybe<String> itemUuid, final ReportPortalClient client, final Scheduler scheduler, final int bufferSize, final boolean convertImages) {
        final LoggingContext context = new LoggingContext(launchUuid, itemUuid, client, scheduler, bufferSize, convertImages);
        LoggingContext.CONTEXT_THREAD_LOCAL.get().push(context);
        return context;
    }
    
    public static Completable complete() {
        final LoggingContext loggingContext = LoggingContext.CONTEXT_THREAD_LOCAL.get().poll();
        if (null != loggingContext) {
            return loggingContext.completed();
        }
        return Maybe.empty().ignoreElement();
    }
    
    LoggingContext(final Maybe<String> launchUuid, final Maybe<String> itemUuid, final ReportPortalClient client, final Scheduler scheduler, final int bufferSize, final boolean convertImages) {
        this.launchUuid = launchUuid;
        this.itemUuid = itemUuid;
        this.emitter = (PublishSubject<Maybe<SaveLogRQ>>)PublishSubject.create();
        this.convertImages = convertImages;
        this.emitter.toFlowable(BackpressureStrategy.BUFFER).flatMap(Maybe::toFlowable).buffer(bufferSize).flatMap(rqs -> client.log(HttpRequestUtils.buildLogMultiPartRequest(rqs)).toFlowable()).doOnError((Consumer)LoggingCallback.LOG_ERROR).observeOn(scheduler).subscribe((FlowableSubscriber)SubscriptionUtils.logFlowableResults("Logging context"));
    }
    
    private SaveLogRQ prepareRequest(final String launchId, final String itemId, final Function<String, SaveLogRQ> logSupplier) throws IOException {
        final SaveLogRQ rq = logSupplier.apply(itemId);
        rq.setLaunchUuid(launchId);
        final SaveLogRQ.File file = rq.getFile();
        if (this.convertImages && null != file && ImageConverter.isImage(file.getContentType())) {
            final TypeAwareByteSource source = ImageConverter.convert(ByteSource.wrap(file.getContent()));
            file.setContent(source.read());
            file.setContentType(source.getMediaType());
        }
        return rq;
    }
    
    public void emit(final Function<String, SaveLogRQ> logSupplier) {
        this.emitter.onNext((Object)this.launchUuid.zipWith((MaybeSource)this.itemUuid, (launchId, itemId) -> this.prepareRequest(launchId, itemId, logSupplier)));
    }
    
    public void emit(final Maybe<String> logItemUuid, final Function<String, SaveLogRQ> logSupplier) {
        this.emitter.onNext((Object)this.launchUuid.zipWith((MaybeSource)logItemUuid, (launchId, itemId) -> this.prepareRequest(launchId, itemId, logSupplier)));
    }
    
    public Completable completed() {
        this.emitter.onComplete();
        return this.emitter.ignoreElements();
    }
    
    static {
        CONTEXT_THREAD_LOCAL = ThreadLocal.withInitial((Supplier<? extends Deque<LoggingContext>>)ArrayDeque::new);
    }
}
