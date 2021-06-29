// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import com.epam.ta.reportportal.ws.model.EntryCreatedAsyncRS;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import io.reactivex.functions.Consumer;

final class LoggingCallback
{
    static final Consumer<OperationCompletionRS> LOG_SUCCESS;
    static final Consumer<Throwable> LOG_ERROR;
    
    private LoggingCallback() {
    }
    
    static Consumer<EntryCreatedAsyncRS> logCreated(final String entry) {
        return (Consumer<EntryCreatedAsyncRS>)(rs -> Launch.LOGGER.debug("ReportPortal {} with ID '{}' has been created", (Object)entry, (Object)rs.getId()));
    }
    
    static {
        LOG_SUCCESS = (rs -> Launch.LOGGER.debug(rs.getResultMessage()));
        LOG_ERROR = (rs -> Launch.LOGGER.error("[{}] ReportPortal execution error", (Object)Thread.currentThread().getId(), (Object)rs));
    }
}
