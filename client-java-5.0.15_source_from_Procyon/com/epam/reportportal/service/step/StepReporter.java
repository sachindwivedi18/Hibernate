// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.step;

import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import java.util.Date;
import java.io.File;
import javax.annotation.Nonnull;
import com.epam.reportportal.listeners.ItemStatus;
import io.reactivex.Maybe;

public interface StepReporter
{
    public static final StepReporter NOOP_STEP_REPORTER = new StepReporter() {
        @Override
        public void setParent(final Maybe<String> parentUuid) {
        }
        
        @Override
        public Maybe<String> getParent() {
            return (Maybe<String>)Maybe.empty();
        }
        
        @Override
        public void removeParent(final Maybe<String> parentUuid) {
        }
        
        @Override
        public boolean isFailed(final Maybe<String> parentId) {
            return false;
        }
        
        @Override
        public void sendStep(final String name) {
        }
        
        @Override
        public void sendStep(final String name, final String... logs) {
        }
        
        @Override
        public void sendStep(@Nonnull final ItemStatus status, final String name) {
        }
        
        @Override
        public void sendStep(@Nonnull final ItemStatus status, final String name, final String... logs) {
        }
        
        @Override
        public void sendStep(@Nonnull final ItemStatus status, final String name, final Throwable throwable) {
        }
        
        @Override
        public void sendStep(final String name, final File... files) {
        }
        
        @Override
        public void sendStep(@Nonnull final ItemStatus status, final String name, final File... files) {
        }
        
        @Override
        public void sendStep(@Nonnull final ItemStatus status, final String name, final Throwable throwable, final File... files) {
        }
        
        @Override
        public void finishPreviousStep() {
        }
    };
    
    void setParent(final Maybe<String> p0);
    
    Maybe<String> getParent();
    
    void removeParent(final Maybe<String> p0);
    
    boolean isFailed(final Maybe<String> p0);
    
    void sendStep(final String p0);
    
    void sendStep(final String p0, final String... p1);
    
    void sendStep(@Nonnull final ItemStatus p0, final String p1);
    
    void sendStep(@Nonnull final ItemStatus p0, final String p1, final String... p2);
    
    void sendStep(@Nonnull final ItemStatus p0, final String p1, final Throwable p2);
    
    void sendStep(final String p0, final File... p1);
    
    void sendStep(@Nonnull final ItemStatus p0, final String p1, final File... p2);
    
    void sendStep(@Nonnull final ItemStatus p0, final String p1, final Throwable p2, final File... p3);
    
    void finishPreviousStep();
    
    public static class StepEntry
    {
        private final Maybe<String> itemId;
        private final Date timestamp;
        private final FinishTestItemRQ finishTestItemRQ;
        
        public StepEntry(final Maybe<String> itemId, final Date timestamp, final FinishTestItemRQ finishTestItemRQ) {
            this.itemId = itemId;
            this.timestamp = timestamp;
            this.finishTestItemRQ = finishTestItemRQ;
        }
        
        public Maybe<String> getItemId() {
            return this.itemId;
        }
        
        public Date getTimestamp() {
            return this.timestamp;
        }
        
        public FinishTestItemRQ getFinishTestItemRQ() {
            return this.finishTestItemRQ;
        }
    }
}
