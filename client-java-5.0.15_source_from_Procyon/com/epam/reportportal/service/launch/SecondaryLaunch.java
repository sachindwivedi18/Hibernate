// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.launch;

import org.slf4j.LoggerFactory;
import io.reactivex.Completable;
import com.epam.reportportal.service.LaunchLoggingContext;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.launch.LaunchResource;
import java.util.concurrent.ConcurrentLinkedQueue;
import io.reactivex.disposables.Disposable;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import com.epam.reportportal.utils.Waiter;
import java.util.concurrent.ExecutorService;
import io.reactivex.Maybe;
import com.epam.reportportal.listeners.ListenerParameters;
import java.util.concurrent.atomic.AtomicReference;
import com.epam.reportportal.service.LockFile;
import com.epam.reportportal.service.ReportPortalClient;
import org.slf4j.Logger;
import com.epam.reportportal.service.LaunchImpl;

public class SecondaryLaunch extends LaunchImpl
{
    private static final Logger LOGGER;
    private final ReportPortalClient rpClient;
    private final LockFile lockFile;
    private final AtomicReference<String> instanceUuid;
    
    public SecondaryLaunch(final ReportPortalClient rpClient, final ListenerParameters parameters, final Maybe<String> launch, final ExecutorService executorService, final LockFile lockFile, final AtomicReference<String> instanceUuid) {
        super(rpClient, parameters, launch, executorService);
        this.rpClient = rpClient;
        this.lockFile = lockFile;
        this.instanceUuid = instanceUuid;
    }
    
    private void waitForLaunchStart() {
        new Waiter("Wait for Launch start").pollingEvery(1L, TimeUnit.SECONDS).timeoutFail().till((Callable<Object>)new Callable<Boolean>() {
            private volatile Boolean result = null;
            private final Queue<Disposable> disposables = new ConcurrentLinkedQueue<Disposable>();
            
            @Override
            public Boolean call() {
                if (this.result == null) {
                    this.disposables.add(SecondaryLaunch.this.launch.subscribe(uuid -> {
                        final Maybe<LaunchResource> maybeRs = SecondaryLaunch.this.rpClient.getLaunchByUuid(uuid);
                        if (maybeRs != null) {
                            this.disposables.add(maybeRs.subscribe(launchResource -> this.result = Boolean.TRUE, throwable -> SecondaryLaunch.LOGGER.debug("Unable to get a Launch: " + throwable.getLocalizedMessage(), throwable)));
                        }
                        else {
                            SecondaryLaunch.LOGGER.debug("RP Client returned 'null' response on get Launch by UUID call");
                        }
                    }));
                }
                else {
                    Disposable disposable;
                    while ((disposable = this.disposables.poll()) != null) {
                        disposable.dispose();
                    }
                }
                return this.result;
            }
        });
    }
    
    @Override
    public Maybe<String> start() {
        if (!this.getParameters().isAsyncReporting()) {
            this.waitForLaunchStart();
        }
        return super.start();
    }
    
    @Override
    public void finish(final FinishExecutionRQ rq) {
        this.QUEUE.getUnchecked(this.launch).addToQueue(LaunchLoggingContext.complete());
        try {
            final Throwable throwable = Completable.concat((Iterable)this.QUEUE.getUnchecked(this.launch).getChildren()).timeout((long)this.getParameters().getReportingTimeout(), TimeUnit.SECONDS).blockingGet();
            if (throwable != null) {
                SecondaryLaunch.LOGGER.error("Unable to finish secondary launch in ReportPortal", throwable);
            }
        }
        finally {
            this.rpClient.close();
            this.lockFile.finishInstanceUuid(this.instanceUuid.get());
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SecondaryLaunch.class);
    }
}
