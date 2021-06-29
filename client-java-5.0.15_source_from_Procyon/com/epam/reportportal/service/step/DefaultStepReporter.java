// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.step;

import org.slf4j.LoggerFactory;
import com.epam.reportportal.message.TypeAwareByteSource;
import java.util.UUID;
import com.epam.reportportal.utils.files.Utils;
import rp.com.google.common.base.Throwables;
import java.io.IOException;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import java.util.Calendar;
import java.util.Date;
import java.util.Collection;
import java.io.File;
import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.listeners.LogLevel;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nonnull;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.reportportal.listeners.ItemStatus;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.ArrayDeque;
import com.epam.reportportal.service.Launch;
import java.util.Set;
import io.reactivex.Maybe;
import java.util.Deque;
import org.slf4j.Logger;

public class DefaultStepReporter implements StepReporter
{
    private static final Logger LOGGER;
    private final ThreadLocal<Deque<Maybe<String>>> parents;
    private final ThreadLocal<Deque<StepEntry>> steps;
    private final Set<Maybe<String>> parentFailures;
    private final Launch launch;
    
    public DefaultStepReporter(final Launch currentLaunch) {
        this.parents = ThreadLocal.withInitial((Supplier<? extends Deque<Maybe<String>>>)ArrayDeque::new);
        this.steps = ThreadLocal.withInitial((Supplier<? extends Deque<StepEntry>>)ArrayDeque::new);
        this.parentFailures = Collections.newSetFromMap(new ConcurrentHashMap<Maybe<String>, Boolean>());
        this.launch = currentLaunch;
    }
    
    @Override
    public void setParent(final Maybe<String> parentUuid) {
        if (parentUuid != null) {
            this.parents.get().add(parentUuid);
        }
    }
    
    @Override
    public Maybe<String> getParent() {
        return this.parents.get().peekLast();
    }
    
    @Override
    public void removeParent(final Maybe<String> parentUuid) {
        if (parentUuid != null) {
            this.parents.get().removeLastOccurrence(parentUuid);
            this.parentFailures.remove(parentUuid);
        }
    }
    
    @Override
    public boolean isFailed(final Maybe<String> parentId) {
        return this.parentFailures.contains(parentId);
    }
    
    protected void sendStep(final ItemStatus status, final String name, final Runnable actions) {
        final StartTestItemRQ rq = this.buildStartStepRequest(name);
        final Maybe<String> stepId = this.startStepRequest(rq);
        if (actions != null) {
            try {
                actions.run();
            }
            catch (Throwable e) {
                DefaultStepReporter.LOGGER.error("Unable to process nested step: " + e.getLocalizedMessage(), e);
            }
        }
        this.finishStepRequest(stepId, status, rq.getStartTime());
    }
    
    @Override
    public void sendStep(final String name) {
        this.sendStep(ItemStatus.PASSED, name, () -> {});
    }
    
    @Override
    public void sendStep(final String name, final String... logs) {
        this.sendStep(ItemStatus.PASSED, name, logs);
    }
    
    @Override
    public void sendStep(@Nonnull final ItemStatus status, final String name) {
        this.sendStep(status, name, () -> {});
    }
    
    @Override
    public void sendStep(@Nonnull final ItemStatus status, final String name, final String... logs) {
        final Runnable actions = Optional.ofNullable(logs).map(l -> () -> Arrays.stream(l).forEach(log -> ReportPortal.emitLog(itemId -> this.buildSaveLogRequest(itemId, log, LogLevel.INFO)))).orElse(null);
        this.sendStep(status, name, actions);
    }
    
    @Override
    public void sendStep(@Nonnull final ItemStatus status, final String name, final Throwable throwable) {
        this.sendStep(status, name, () -> ReportPortal.emitLog(itemId -> this.buildSaveLogRequest(itemId, throwable)));
    }
    
    @Override
    public void sendStep(final String name, final File... files) {
        this.sendStep(ItemStatus.PASSED, name, files);
    }
    
    @Override
    public void sendStep(@Nonnull final ItemStatus status, final String name, final File... files) {
        final Runnable actions = Optional.ofNullable(files).map(f -> () -> Arrays.stream(f).forEach(file -> ReportPortal.emitLog(itemId -> this.buildSaveLogRequest(itemId, "", LogLevel.INFO, file)))).orElse(null);
        this.sendStep(status, name, actions);
    }
    
    @Override
    public void sendStep(@Nonnull final ItemStatus status, final String name, final Throwable throwable, final File... files) {
        int length;
        int i = 0;
        File file;
        this.sendStep(status, name, () -> {
            for (length = files.length; i < length; ++i) {
                file = files[i];
                ReportPortal.emitLog(itemId -> this.buildSaveLogRequest(itemId, throwable, file));
            }
        });
    }
    
    private Optional<StepEntry> finishPreviousStepInternal() {
        return Optional.ofNullable(this.steps.get().poll()).map(stepEntry -> {
            this.launch.finishTestItem(stepEntry.getItemId(), stepEntry.getFinishTestItemRQ());
            return stepEntry;
        });
    }
    
    @Override
    public void finishPreviousStep() {
        this.finishPreviousStepInternal().ifPresent(e -> {
            if (ItemStatus.FAILED.name().equalsIgnoreCase(e.getFinishTestItemRQ().getStatus())) {
                this.parentFailures.addAll(this.parents.get());
            }
        });
    }
    
    private Maybe<String> startStepRequest(final StartTestItemRQ startTestItemRQ) {
        final Date previousDate;
        final Date currentDate;
        this.finishPreviousStepInternal().ifPresent(e -> {
            previousDate = e.getTimestamp();
            currentDate = startTestItemRQ.getStartTime();
            if (!previousDate.before(currentDate)) {
                startTestItemRQ.setStartTime(new Date(previousDate.getTime() + 1L));
            }
            if (ItemStatus.FAILED.name().equalsIgnoreCase(e.getFinishTestItemRQ().getStatus())) {
                this.parentFailures.addAll(this.parents.get());
            }
            return;
        });
        return this.launch.startTestItem(this.parents.get().getLast(), startTestItemRQ);
    }
    
    private StartTestItemRQ buildStartStepRequest(final String name) {
        final StartTestItemRQ startTestItemRQ = new StartTestItemRQ();
        startTestItemRQ.setName(name);
        startTestItemRQ.setType("STEP");
        startTestItemRQ.setHasStats(false);
        startTestItemRQ.setStartTime(Calendar.getInstance().getTime());
        return startTestItemRQ;
    }
    
    private void finishStepRequest(final Maybe<String> stepId, final ItemStatus status, final Date timestamp) {
        final FinishTestItemRQ finishTestItemRQ = this.buildFinishTestItemRequest(status, Calendar.getInstance().getTime());
        this.steps.get().add(new StepEntry(stepId, timestamp, finishTestItemRQ));
    }
    
    private FinishTestItemRQ buildFinishTestItemRequest(final ItemStatus status, final Date endTime) {
        final FinishTestItemRQ finishTestItemRQ = new FinishTestItemRQ();
        finishTestItemRQ.setStatus(status.name());
        finishTestItemRQ.setEndTime(endTime);
        return finishTestItemRQ;
    }
    
    private SaveLogRQ buildSaveLogRequest(final String itemId, final String message, final LogLevel level) {
        final SaveLogRQ rq = new SaveLogRQ();
        rq.setItemUuid(itemId);
        rq.setMessage(message);
        rq.setLevel(level.name());
        rq.setLogTime(Calendar.getInstance().getTime());
        return rq;
    }
    
    private SaveLogRQ buildSaveLogRequest(final String itemId, final String message, final LogLevel level, final File file) {
        final SaveLogRQ logRQ = this.buildSaveLogRequest(itemId, message, level);
        if (file != null) {
            try {
                logRQ.setFile(this.createFileModel(file));
            }
            catch (IOException e) {
                DefaultStepReporter.LOGGER.error("Unable to read file attachment: " + e.getMessage(), (Throwable)e);
            }
        }
        return logRQ;
    }
    
    private SaveLogRQ buildSaveLogRequest(final String itemId, final Throwable throwable, final File file) {
        final String message = (throwable != null) ? Throwables.getStackTraceAsString(throwable) : "Test has failed without exception";
        return this.buildSaveLogRequest(itemId, message, LogLevel.ERROR, file);
    }
    
    private SaveLogRQ buildSaveLogRequest(final String itemId, final Throwable throwable) {
        return this.buildSaveLogRequest(itemId, throwable, null);
    }
    
    private SaveLogRQ.File createFileModel(final File file) throws IOException {
        final TypeAwareByteSource dataSource = Utils.getFile(file);
        final SaveLogRQ.File fileModel = new SaveLogRQ.File();
        fileModel.setContent(dataSource.read());
        fileModel.setContentType(dataSource.getMediaType());
        fileModel.setName(UUID.randomUUID().toString());
        return fileModel;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)DefaultStepReporter.class);
    }
}
