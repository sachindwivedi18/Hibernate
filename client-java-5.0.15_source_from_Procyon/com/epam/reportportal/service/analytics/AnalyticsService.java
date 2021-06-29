// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.analytics;

import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import java.util.Set;
import com.epam.reportportal.service.analytics.item.AnalyticsItem;
import java.util.concurrent.TimeUnit;
import com.epam.reportportal.utils.properties.DefaultProperties;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.function.Function;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributeResource;
import com.epam.reportportal.utils.properties.SystemAttributesExtractor;
import com.epam.reportportal.utils.properties.ClientProperties;
import com.epam.reportportal.utils.properties.PropertyHolder;
import com.epam.reportportal.service.analytics.item.AnalyticsEvent;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import io.reactivex.Maybe;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executors;
import com.epam.reportportal.listeners.ListenerParameters;
import io.reactivex.Completable;
import java.util.List;
import io.reactivex.Scheduler;
import java.util.concurrent.ExecutorService;
import java.io.Closeable;

public class AnalyticsService implements Closeable
{
    private static final String CLIENT_PROPERTIES_FILE = "client.properties";
    private static final String START_LAUNCH_EVENT_ACTION = "Start launch";
    private static final String CLIENT_VALUE_FORMAT = "Client name \"%s\", version \"%s\"";
    private static final String AGENT_VALUE_FORMAT = "Agent name \"%s\", version \"%s\"";
    private final ExecutorService googleAnalyticsExecutor;
    private final Scheduler scheduler;
    private final GoogleAnalytics googleAnalytics;
    private final List<Completable> dependencies;
    private final ListenerParameters parameters;
    
    public AnalyticsService(final ListenerParameters listenerParameters) {
        this.googleAnalyticsExecutor = Executors.newSingleThreadExecutor();
        this.scheduler = Schedulers.from((Executor)this.googleAnalyticsExecutor);
        this.dependencies = new CopyOnWriteArrayList<Completable>();
        this.parameters = listenerParameters;
        this.googleAnalytics = new GoogleAnalytics("UA-173456809-1", this.parameters.getProxyUrl());
    }
    
    protected GoogleAnalytics getGoogleAnalytics() {
        return this.googleAnalytics;
    }
    
    public void sendEvent(final Maybe<String> launchIdMaybe, final StartLaunchRQ rq) {
        final AnalyticsEvent.AnalyticsEventBuilder analyticsEventBuilder = AnalyticsEvent.builder().withAction("Start launch");
        SystemAttributesExtractor.extract("client.properties", this.getClass().getClassLoader(), ClientProperties.CLIENT).stream().map((Function<? super Object, ?>)ItemAttributeResource::getValue).map(a -> a.split(Pattern.quote("|"))).filter(a -> a.length >= 2).findFirst().ifPresent(clientAttribute -> analyticsEventBuilder.withCategory(String.format("Client name \"%s\", version \"%s\"", (Object[])clientAttribute)));
        Optional.ofNullable(rq.getAttributes()).flatMap(r -> r.stream().filter(attribute -> attribute.isSystem() && DefaultProperties.AGENT.getName().equalsIgnoreCase(attribute.getKey())).findAny()).map((Function<? super Object, ?>)ItemAttributeResource::getValue).map(a -> a.split(Pattern.quote("|"))).filter(a -> a.length >= 2).ifPresent(agentAttribute -> analyticsEventBuilder.withLabel(String.format("Agent name \"%s\", version \"%s\"", (Object[])agentAttribute)));
        final Maybe<Boolean> analyticsMaybe = (Maybe<Boolean>)launchIdMaybe.map(l -> this.getGoogleAnalytics().send(analyticsEventBuilder.build())).subscribeOn(this.scheduler);
        this.dependencies.add(analyticsMaybe.ignoreElement());
    }
    
    @Override
    public void close() {
        try {
            final Throwable result = Completable.concat((Iterable)this.dependencies).timeout((long)this.parameters.getReportingTimeout(), TimeUnit.SECONDS).blockingGet();
            if (result != null) {
                throw new RuntimeException("Unable to complete execution of all dependencies", result);
            }
        }
        finally {
            this.googleAnalyticsExecutor.shutdown();
            try {
                if (!this.googleAnalyticsExecutor.awaitTermination(this.parameters.getReportingTimeout(), TimeUnit.SECONDS)) {
                    this.googleAnalyticsExecutor.shutdownNow();
                }
            }
            catch (InterruptedException ex) {}
            this.getGoogleAnalytics().close();
        }
    }
}
