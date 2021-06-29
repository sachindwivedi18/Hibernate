// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.launch;

import java.util.UUID;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import java.util.concurrent.ExecutorService;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.ReportPortalClient;
import java.util.concurrent.atomic.AtomicReference;
import com.epam.reportportal.service.LockFile;
import com.epam.reportportal.service.LaunchImpl;

public class PrimaryLaunch extends LaunchImpl
{
    private final LockFile lockFile;
    private final AtomicReference<String> instanceUuid;
    
    public PrimaryLaunch(final ReportPortalClient rpClient, final ListenerParameters parameters, final StartLaunchRQ launch, final ExecutorService executorService, final LockFile lockFile, final AtomicReference<String> instanceUuid) {
        super(rpClient, parameters, launch, executorService);
        this.lockFile = lockFile;
        this.instanceUuid = instanceUuid;
    }
    
    @Override
    public void finish(final FinishExecutionRQ rq) {
        try {
            super.finish(rq);
        }
        finally {
            this.lockFile.finishInstanceUuid(this.instanceUuid.get());
            this.instanceUuid.set(UUID.randomUUID().toString());
        }
    }
}
