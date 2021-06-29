// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.analytics.item;

import rp.com.google.common.collect.Maps;
import io.reactivex.annotations.Nullable;
import java.util.Map;

public class AnalyticsEvent implements AnalyticsItem
{
    private static final String TYPE = "event";
    private final Map<String, String> params;
    
    public AnalyticsEvent(@Nullable final String eventCategory, @Nullable final String eventAction, @Nullable final String eventLabel) {
        (this.params = (Map<String, String>)Maps.newHashMapWithExpectedSize(4)).put("t", "event");
        this.params.put("ec", eventCategory);
        this.params.put("ea", eventAction);
        this.params.put("el", eventLabel);
    }
    
    private AnalyticsEvent(final Map<String, String> params) {
        this.params = params;
    }
    
    public static AnalyticsEventBuilder builder() {
        return new AnalyticsEventBuilder();
    }
    
    @Override
    public Map<String, String> getParams() {
        return this.params;
    }
    
    public static class AnalyticsEventBuilder
    {
        private final Map<String, String> params;
        
        public AnalyticsEventBuilder() {
            (this.params = (Map<String, String>)Maps.newHashMapWithExpectedSize(4)).put("t", "event");
        }
        
        public AnalyticsEventBuilder withCategory(final String category) {
            this.params.put("ec", category);
            return this;
        }
        
        public AnalyticsEventBuilder withAction(final String action) {
            this.params.put("ea", action);
            return this;
        }
        
        public AnalyticsEventBuilder withLabel(final String label) {
            this.params.put("el", label);
            return this;
        }
        
        public AnalyticsEvent build() {
            return new AnalyticsEvent(this.params, null);
        }
    }
}
