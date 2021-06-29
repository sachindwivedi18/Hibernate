// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import com.epam.reportportal.utils.properties.PropertiesLoader;
import java.util.concurrent.TimeUnit;
import rp.org.apache.http.conn.ConnectionKeepAliveStrategy;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import rp.org.apache.http.client.HttpRequestRetryHandler;
import rp.org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import rp.org.apache.http.client.config.RequestConfig;
import rp.org.apache.http.HttpHost;
import rp.org.apache.http.ssl.TrustStrategy;
import rp.org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import com.epam.reportportal.utils.SslUtils;
import rp.org.apache.http.ssl.SSLContextBuilder;
import com.epam.reportportal.utils.properties.ListenerProperty;
import rp.org.apache.http.impl.client.HttpClients;
import java.net.MalformedURLException;
import java.net.URL;
import com.epam.reportportal.restendpoint.http.ErrorHandler;
import java.util.List;
import com.epam.reportportal.restendpoint.http.HttpClientRestEndpoint;
import com.epam.reportportal.restendpoint.serializer.ByteArraySerializer;
import com.epam.reportportal.restendpoint.serializer.Serializer;
import java.util.LinkedList;
import com.epam.reportportal.restendpoint.serializer.json.JacksonSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.epam.reportportal.restendpoint.http.RestEndpoint;
import com.epam.reportportal.restendpoint.http.RestEndpoints;
import rp.org.apache.http.HttpRequestInterceptor;
import rp.org.apache.http.client.HttpClient;
import rp.org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import io.reactivex.MaybeEmitter;
import java.util.concurrent.Executors;
import rp.com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.epam.reportportal.message.TypeAwareByteSource;
import com.epam.reportportal.message.ReportPortalMessage;
import com.epam.reportportal.utils.files.Utils;
import com.epam.reportportal.utils.MimeTypeDetector;
import java.io.File;
import java.util.Date;
import java.util.Deque;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import java.util.function.Function;
import java.util.Optional;
import com.epam.reportportal.service.launch.SecondaryLaunch;
import io.reactivex.Maybe;
import java.io.IOException;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import com.epam.reportportal.service.launch.PrimaryLaunch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.BooleanUtils;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import com.epam.reportportal.listeners.ListenerParameters;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;

public class ReportPortal
{
    private static final Logger LOGGER;
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final AtomicReference<String> instanceUuid;
    private final ListenerParameters parameters;
    private final LockFile lockFile;
    private final ReportPortalClient rpClient;
    private final ExecutorService executor;
    
    ReportPortal(@Nullable final ReportPortalClient rpClient, @Nonnull final ExecutorService executor, @Nonnull final ListenerParameters parameters, @Nullable final LockFile lockFile) {
        this.instanceUuid = new AtomicReference<String>(UUID.randomUUID().toString());
        this.rpClient = rpClient;
        this.executor = executor;
        this.parameters = parameters;
        this.lockFile = lockFile;
    }
    
    public Launch newLaunch(final StartLaunchRQ rq) {
        if (BooleanUtils.isNotTrue(this.parameters.getEnable()) || this.rpClient == null) {
            return Launch.NOOP_LAUNCH;
        }
        if (this.lockFile == null) {
            return new LaunchImpl(this.rpClient, this.parameters, rq, this.executor);
        }
        final String uuid = this.lockFile.obtainLaunchUuid(this.instanceUuid.get());
        if (uuid == null) {
            return new LaunchImpl(this.rpClient, this.parameters, rq, this.executor);
        }
        if (this.instanceUuid.get().equals(uuid)) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try {
                final StartLaunchRQ rqCopy = (StartLaunchRQ)objectMapper.readValue(objectMapper.writeValueAsString((Object)rq), (Class)StartLaunchRQ.class);
                rqCopy.setUuid(uuid);
                return new PrimaryLaunch(this.rpClient, this.parameters, rqCopy, this.executor, this.lockFile, this.instanceUuid);
            }
            catch (IOException e) {
                throw new InternalReportPortalClientException("Unable to clone start launch request:", e);
            }
        }
        final Maybe<String> launch = (Maybe<String>)Maybe.create(emitter -> {
            emitter.onSuccess((Object)uuid);
            emitter.onComplete();
        });
        return new SecondaryLaunch(this.rpClient, this.parameters, launch, this.executor, this.lockFile, this.instanceUuid);
    }
    
    public Launch withLaunch(final Maybe<String> launchUuid) {
        return Optional.ofNullable(this.rpClient).map(c -> new LaunchImpl(c, this.parameters, launchUuid, this.executor)).orElse(Launch.NOOP_LAUNCH);
    }
    
    public ListenerParameters getParameters() {
        return this.parameters;
    }
    
    public ReportPortalClient getClient() {
        return this.rpClient;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private static LockFile getLockFile(final ListenerParameters parameters) {
        if (parameters.getClientJoin()) {
            return new LockFile(parameters);
        }
        return null;
    }
    
    public static ReportPortal create(final ReportPortalClient client, final ListenerParameters params) {
        return create(client, params, buildExecutorService(params));
    }
    
    public static ReportPortal create(@Nonnull final ReportPortalClient client, @Nonnull final ListenerParameters params, @Nonnull final ExecutorService executor) {
        return new ReportPortal(client, executor, params, getLockFile(params));
    }
    
    public static boolean emitLog(final Function<String, SaveLogRQ> logSupplier) {
        final LoggingContext loggingContext = LoggingContext.CONTEXT_THREAD_LOCAL.get().peek();
        if (null != loggingContext) {
            loggingContext.emit(logSupplier);
            return true;
        }
        return false;
    }
    
    public static boolean emitLaunchLog(final Function<String, SaveLogRQ> logSupplier) {
        final LaunchLoggingContext launchLoggingContext = LaunchLoggingContext.loggingContextMap.get("default");
        if (null != launchLoggingContext) {
            launchLoggingContext.emit(logSupplier);
            return true;
        }
        return false;
    }
    
    public static boolean emitLog(final Maybe<String> itemUuid, final Function<String, SaveLogRQ> logSupplier) {
        final LoggingContext loggingContext = LoggingContext.CONTEXT_THREAD_LOCAL.get().peek();
        if (null != loggingContext) {
            loggingContext.emit(itemUuid, logSupplier);
            return true;
        }
        return false;
    }
    
    public static boolean emitLog(final String message, final String level, final Date time) {
        final SaveLogRQ rq;
        return emitLog(itemUuid -> {
            rq = new SaveLogRQ();
            rq.setLevel(level);
            rq.setLogTime(time);
            rq.setItemUuid(itemUuid);
            rq.setMessage(message);
            return rq;
        });
    }
    
    public static boolean emitLaunchLog(final String message, final String level, final Date time) {
        final SaveLogRQ rq;
        return emitLaunchLog(launchUuid -> {
            rq = new SaveLogRQ();
            rq.setLevel(level);
            rq.setLogTime(time);
            rq.setLaunchUuid(launchUuid);
            rq.setMessage(message);
            return rq;
        });
    }
    
    private static void fillSaveLogRQ(final SaveLogRQ rq, final String message, final String level, final Date time, final File file) {
        rq.setMessage(message);
        rq.setLevel(level);
        rq.setLogTime(time);
        try {
            final SaveLogRQ.File f = new SaveLogRQ.File();
            f.setContentType(MimeTypeDetector.detect(file));
            f.setContent(Utils.readFileToBytes(file));
            f.setName(UUID.randomUUID().toString());
            rq.setFile(f);
        }
        catch (IOException e) {
            ReportPortal.LOGGER.error("Cannot send file to ReportPortal", (Throwable)e);
        }
    }
    
    public static boolean emitLog(final String message, final String level, final Date time, final File file) {
        final SaveLogRQ rq;
        return emitLog(itemUuid -> {
            rq = new SaveLogRQ();
            rq.setItemUuid(itemUuid);
            fillSaveLogRQ(rq, message, level, time, file);
            return rq;
        });
    }
    
    public static boolean emitLaunchLog(final String message, final String level, final Date time, final File file) {
        final SaveLogRQ rq;
        return emitLaunchLog(launchUuid -> {
            rq = new SaveLogRQ();
            rq.setLaunchUuid(launchUuid);
            fillSaveLogRQ(rq, message, level, time, file);
            return rq;
        });
    }
    
    private static void fillSaveLogRQ(final SaveLogRQ rq, final String level, final Date time, final ReportPortalMessage message) {
        rq.setLevel(level);
        rq.setLogTime(time);
        rq.setMessage(message.getMessage());
        try {
            final TypeAwareByteSource data = message.getData();
            final SaveLogRQ.File file = new SaveLogRQ.File();
            file.setContent(data.read());
            file.setContentType(data.getMediaType());
            file.setName(UUID.randomUUID().toString());
            rq.setFile(file);
        }
        catch (Exception e) {
            ReportPortal.LOGGER.error("Cannot send file to ReportPortal", (Throwable)e);
        }
    }
    
    public static boolean emitLog(final ReportPortalMessage message, final String level, final Date time) {
        final SaveLogRQ rq;
        return emitLog(itemUuid -> {
            rq = new SaveLogRQ();
            rq.setItemUuid(itemUuid);
            fillSaveLogRQ(rq, level, time, message);
            return rq;
        });
    }
    
    public static boolean emitLaunchLog(final ReportPortalMessage message, final String level, final Date time) {
        final SaveLogRQ rq;
        return emitLaunchLog(launchUuid -> {
            rq = new SaveLogRQ();
            rq.setLaunchUuid(launchUuid);
            fillSaveLogRQ(rq, level, time, message);
            return rq;
        });
    }
    
    private static ExecutorService buildExecutorService(final ListenerParameters params) {
        return Executors.newFixedThreadPool(params.getIoPoolSize(), new ThreadFactoryBuilder().setNameFormat("rp-io-%s").build());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ReportPortal.class);
    }
    
    public static class Builder
    {
        static final String API_V1_BASE = "/api/v1";
        static final String API_V2_BASE = "/api/v2";
        private static final String HTTPS = "https";
        private HttpClientBuilder httpClient;
        private ListenerParameters parameters;
        private ExecutorService executor;
        
        public Builder withHttpClient(final HttpClientBuilder client) {
            this.httpClient = client;
            return this;
        }
        
        public Builder withParameters(final ListenerParameters parameters) {
            this.parameters = parameters;
            return this;
        }
        
        public Builder withExecutorService(final ExecutorService executor) {
            this.executor = executor;
            return this;
        }
        
        public ReportPortal build() {
            try {
                final ListenerParameters params = Optional.ofNullable(this.parameters).orElse(new ListenerParameters(this.defaultPropertiesLoader()));
                final ExecutorService executorService = (this.executor == null) ? this.buildExecutorService(params) : this.executor;
                return new ReportPortal(this.buildClient(ReportPortalClient.class, params, executorService), executorService, params, this.buildLockFile(params));
            }
            catch (Exception e) {
                final String errMsg = "Cannot build ReportPortal client";
                ReportPortal.LOGGER.error(errMsg, (Throwable)e);
                throw new InternalReportPortalClientException(errMsg, e);
            }
        }
        
        public <T extends ReportPortalClient> T buildClient(@Nonnull final Class<T> clientType, @Nonnull final ListenerParameters params) {
            return this.buildClient(clientType, params, this.buildExecutorService(params));
        }
        
        public <T extends ReportPortalClient> T buildClient(@Nonnull final Class<T> clientType, @Nonnull final ListenerParameters params, @Nonnull final ExecutorService executor) {
            try {
                final HttpClient client = Optional.ofNullable(this.httpClient).map(c -> c.addInterceptorLast(new BearerAuthInterceptor(params.getApiKey())).build()).orElseGet(() -> this.defaultClient(params));
                return Optional.ofNullable(client).map(c -> RestEndpoints.forInterface(clientType, this.buildRestEndpoint(params, c, executor))).orElse(null);
            }
            catch (Exception e) {
                final String errMsg = "Cannot build ReportPortal client";
                ReportPortal.LOGGER.error(errMsg, (Throwable)e);
                throw new InternalReportPortalClientException(errMsg, e);
            }
        }
        
        protected RestEndpoint buildRestEndpoint(@Nonnull final ListenerParameters parameters, @Nonnull final HttpClient client) {
            return this.buildRestEndpoint(parameters, client, this.buildExecutorService(parameters));
        }
        
        protected RestEndpoint buildRestEndpoint(@Nonnull final ListenerParameters parameters, @Nonnull final HttpClient client, @Nonnull final ExecutorService executor) {
            final ObjectMapper om = new ObjectMapper();
            om.setDateFormat((DateFormat)new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final String baseUrl = parameters.getBaseUrl();
            final String project = parameters.getProjectName();
            final JacksonSerializer jacksonSerializer = new JacksonSerializer(om);
            return new HttpClientRestEndpoint(client, new LinkedList<Serializer>() {
                {
                    ((LinkedList<JacksonSerializer>)this).add(jacksonSerializer);
                    ((LinkedList<ByteArraySerializer>)this).add(new ByteArraySerializer());
                }
            }, new ReportPortalErrorHandler(jacksonSerializer), this.buildEndpointUrl(baseUrl, project, parameters.isAsyncReporting()), executor);
        }
        
        protected String buildEndpointUrl(final String baseUrl, final String project, final boolean asyncReporting) {
            final String apiBase = asyncReporting ? "/api/v2" : "/api/v1";
            return baseUrl + apiBase + "/" + project;
        }
        
        protected HttpClient defaultClient(final ListenerParameters parameters) {
            final String baseUrlStr = parameters.getBaseUrl();
            if (baseUrlStr == null) {
                ReportPortal.LOGGER.warn("Base url for Report Portal server is not set!");
                return null;
            }
            URL baseUrl;
            try {
                baseUrl = new URL(baseUrlStr);
            }
            catch (MalformedURLException e) {
                ReportPortal.LOGGER.warn("Unable to parse Report Portal URL", (Throwable)e);
                return null;
            }
            final String keyStore = parameters.getKeystore();
            final String keyStorePassword = parameters.getKeystorePassword();
            final HttpClientBuilder builder = HttpClients.custom();
            if ("https".equals(baseUrl.getProtocol()) && keyStore != null) {
                if (null == keyStorePassword) {
                    throw new InternalReportPortalClientException("You should provide keystore password parameter [" + ListenerProperty.KEYSTORE_PASSWORD + "] if you use HTTPS protocol");
                }
                try {
                    builder.setSSLContext(SSLContextBuilder.create().loadTrustMaterial(SslUtils.loadKeyStore(keyStore, keyStorePassword), TrustSelfSignedStrategy.INSTANCE).build());
                }
                catch (Exception e2) {
                    throw new InternalReportPortalClientException("Unable to load trust store");
                }
            }
            final String proxyUrl = parameters.getProxyUrl();
            if (proxyUrl != null) {
                builder.setProxy(HttpHost.create(proxyUrl));
            }
            builder.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec("standard").build()).setRetryHandler(new StandardHttpRequestRetryHandler(parameters.getTransferRetries(), true)).setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                    final long keepAliveDuration = super.getKeepAliveDuration(response, context);
                    if (keepAliveDuration == -1L) {
                        return parameters.getMaxConnectionTtlMs();
                    }
                    return keepAliveDuration;
                }
            }).setMaxConnPerRoute(parameters.getMaxConnectionsPerRoute()).setMaxConnTotal(parameters.getMaxConnectionsTotal()).setConnectionTimeToLive(parameters.getMaxConnectionTtlMs(), TimeUnit.MILLISECONDS).evictIdleConnections(parameters.getMaxConnectionIdleTtlMs(), TimeUnit.MILLISECONDS);
            return builder.addInterceptorLast(new BearerAuthInterceptor(parameters.getApiKey())).build();
        }
        
        protected LockFile buildLockFile(final ListenerParameters parameters) {
            return getLockFile(parameters);
        }
        
        protected PropertiesLoader defaultPropertiesLoader() {
            return PropertiesLoader.load();
        }
        
        protected ExecutorService buildExecutorService(final ListenerParameters params) {
            return buildExecutorService(params);
        }
    }
}
