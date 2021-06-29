// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.listeners;

import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import rp.com.google.common.annotations.VisibleForTesting;
import com.epam.reportportal.utils.AttributeParser;
import java.util.function.Function;
import java.util.Optional;
import com.epam.reportportal.utils.properties.ListenerProperty;
import com.epam.reportportal.utils.properties.PropertiesLoader;
import rp.com.google.common.collect.Sets;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import java.util.Set;
import com.epam.ta.reportportal.ws.model.launch.Mode;

public class ListenerParameters implements Cloneable
{
    private static final int DEFAULT_REPORTING_TIMEOUT = 300;
    private static final int DEFAULT_IO_POOL_SIZE = 100;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;
    private static final int DEFAULT_MAX_CONNECTIONS_TOTAL = 100;
    private static final boolean DEFAULT_ENABLE = true;
    private static final boolean DEFAULT_SKIP_ISSUE = true;
    private static final boolean DEFAULT_CONVERT_IMAGE = false;
    private static final boolean DEFAULT_RETURN = false;
    private static final boolean DEFAULT_ASYNC_REPORTING = false;
    private static final boolean DEFAULT_CALLBACK_REPORTING_ENABLED = false;
    private static final int DEFAULT_MAX_CONNECTION_TIME_TO_LIVE_MS = 29900;
    private static final int DEFAULT_MAX_CONNECTION_IDLE_TIME_MS = 5000;
    private static final int DEFAULT_TRANSFER_RETRY_COUNT = 5;
    private static final boolean DEFAULT_CLIENT_JOIN_MODE = true;
    private static final String DEFAULT_LOCK_FILE_NAME = "reportportal.lock";
    private static final String DEFAULT_SYNC_FILE_NAME = "reportportal.sync";
    private static final long DEFAULT_FILE_WAIT_TIMEOUT_MS;
    private String description;
    private String apiKey;
    private String baseUrl;
    private String proxyUrl;
    private String projectName;
    private String launchName;
    private Mode launchRunningMode;
    private Set<ItemAttributesRQ> attributes;
    private Boolean enable;
    private Boolean isSkippedAnIssue;
    private Integer batchLogsSize;
    private boolean convertImage;
    private Integer reportingTimeout;
    private String keystore;
    private String keystorePassword;
    private boolean rerun;
    private String rerunOf;
    private boolean asyncReporting;
    private boolean callbackReportingEnabled;
    private Integer ioPoolSize;
    private Integer maxConnectionsPerRoute;
    private Integer maxConnectionsTotal;
    private Integer maxConnectionTtlMs;
    private Integer maxConnectionIdleTtlMs;
    private Integer transferRetries;
    private boolean clientJoin;
    private String lockFileName;
    private String syncFileName;
    private long fileWaitTimeout;
    
    public ListenerParameters() {
        this.isSkippedAnIssue = true;
        this.batchLogsSize = 10;
        this.convertImage = false;
        this.reportingTimeout = 300;
        this.attributes = (Set<ItemAttributesRQ>)Sets.newHashSet();
        this.rerun = false;
        this.asyncReporting = false;
        this.callbackReportingEnabled = false;
        this.ioPoolSize = 100;
        this.maxConnectionsPerRoute = 50;
        this.maxConnectionsTotal = 100;
        this.maxConnectionTtlMs = 29900;
        this.maxConnectionIdleTtlMs = 5000;
        this.transferRetries = 5;
        this.clientJoin = true;
        this.lockFileName = "reportportal.lock";
        this.syncFileName = "reportportal.sync";
        this.fileWaitTimeout = ListenerParameters.DEFAULT_FILE_WAIT_TIMEOUT_MS;
    }
    
    public ListenerParameters(final PropertiesLoader properties) {
        this.description = properties.getProperty(ListenerProperty.DESCRIPTION);
        this.apiKey = Optional.ofNullable(properties.getProperty(ListenerProperty.API_KEY, properties.getProperty(ListenerProperty.UUID))).map((Function<? super String, ? extends String>)String::trim).orElse(null);
        this.baseUrl = ((properties.getProperty(ListenerProperty.BASE_URL) != null) ? properties.getProperty(ListenerProperty.BASE_URL).trim() : null);
        this.proxyUrl = properties.getProperty(ListenerProperty.HTTP_PROXY_URL);
        this.projectName = ((properties.getProperty(ListenerProperty.PROJECT_NAME) != null) ? properties.getProperty(ListenerProperty.PROJECT_NAME).trim() : null);
        this.launchName = properties.getProperty(ListenerProperty.LAUNCH_NAME);
        this.attributes = AttributeParser.parseAsSet(properties.getProperty(ListenerProperty.LAUNCH_ATTRIBUTES));
        this.launchRunningMode = this.parseLaunchMode(properties.getProperty(ListenerProperty.MODE));
        this.enable = properties.getPropertyAsBoolean(ListenerProperty.ENABLE, true);
        this.isSkippedAnIssue = properties.getPropertyAsBoolean(ListenerProperty.SKIPPED_AS_ISSUE, true);
        this.batchLogsSize = properties.getPropertyAsInt(ListenerProperty.BATCH_SIZE_LOGS, 10);
        this.convertImage = properties.getPropertyAsBoolean(ListenerProperty.IS_CONVERT_IMAGE, false);
        this.reportingTimeout = properties.getPropertyAsInt(ListenerProperty.REPORTING_TIMEOUT, 300);
        this.keystore = properties.getProperty(ListenerProperty.KEYSTORE_RESOURCE);
        this.keystorePassword = properties.getProperty(ListenerProperty.KEYSTORE_PASSWORD);
        this.rerun = properties.getPropertyAsBoolean(ListenerProperty.RERUN, false);
        this.rerunOf = properties.getProperty(ListenerProperty.RERUN_OF);
        this.asyncReporting = properties.getPropertyAsBoolean(ListenerProperty.ASYNC_REPORTING, false);
        this.callbackReportingEnabled = properties.getPropertyAsBoolean(ListenerProperty.CALLBACK_REPORTING_ENABLED, false);
        this.ioPoolSize = properties.getPropertyAsInt(ListenerProperty.IO_POOL_SIZE, 100);
        this.maxConnectionsPerRoute = properties.getPropertyAsInt(ListenerProperty.MAX_CONNECTIONS_PER_ROUTE, 50);
        this.maxConnectionsTotal = properties.getPropertyAsInt(ListenerProperty.MAX_CONNECTIONS_TOTAL, 100);
        this.maxConnectionTtlMs = properties.getPropertyAsInt(ListenerProperty.MAX_CONNECTION_TIME_TO_LIVE, 29900);
        this.maxConnectionIdleTtlMs = properties.getPropertyAsInt(ListenerProperty.MAX_CONNECTION_IDLE_TIME, 5000);
        this.transferRetries = properties.getPropertyAsInt(ListenerProperty.MAX_TRANSFER_RETRY_COUNT, 5);
        this.clientJoin = properties.getPropertyAsBoolean(ListenerProperty.CLIENT_JOIN_MODE, true);
        this.lockFileName = properties.getProperty(ListenerProperty.LOCK_FILE_NAME, "reportportal.lock");
        this.syncFileName = properties.getProperty(ListenerProperty.SYNC_FILE_NAME, "reportportal.sync");
        this.fileWaitTimeout = properties.getPropertyAsInt(ListenerProperty.FILE_WAIT_TIMEOUT_MS, (int)ListenerParameters.DEFAULT_FILE_WAIT_TIMEOUT_MS);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getApiKey() {
        return this.apiKey;
    }
    
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getProxyUrl() {
        return this.proxyUrl;
    }
    
    public void setProxyUrl(final String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
    
    public String getProjectName() {
        return this.projectName;
    }
    
    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }
    
    public String getLaunchName() {
        return this.launchName;
    }
    
    public void setLaunchName(final String launchName) {
        this.launchName = launchName;
    }
    
    public Mode getLaunchRunningMode() {
        return this.launchRunningMode;
    }
    
    public void setLaunchRunningMode(final Mode launchRunningMode) {
        this.launchRunningMode = launchRunningMode;
    }
    
    public Set<ItemAttributesRQ> getAttributes() {
        return this.attributes;
    }
    
    public void setAttributes(final Set<ItemAttributesRQ> attributes) {
        this.attributes = attributes;
    }
    
    public Boolean getEnable() {
        return this.enable;
    }
    
    public void setEnable(final Boolean enable) {
        this.enable = enable;
    }
    
    public Boolean getSkippedAnIssue() {
        return this.isSkippedAnIssue;
    }
    
    public void setSkippedAnIssue(final Boolean skippedAnIssue) {
        this.isSkippedAnIssue = skippedAnIssue;
    }
    
    public Integer getBatchLogsSize() {
        return this.batchLogsSize;
    }
    
    public void setBatchLogsSize(final Integer batchLogsSize) {
        this.batchLogsSize = batchLogsSize;
    }
    
    public boolean isConvertImage() {
        return this.convertImage;
    }
    
    public void setConvertImage(final boolean convertImage) {
        this.convertImage = convertImage;
    }
    
    public Integer getReportingTimeout() {
        return this.reportingTimeout;
    }
    
    public String getKeystore() {
        return this.keystore;
    }
    
    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    }
    
    public void setReportingTimeout(final Integer reportingTimeout) {
        this.reportingTimeout = reportingTimeout;
    }
    
    public String getKeystorePassword() {
        return this.keystorePassword;
    }
    
    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }
    
    public boolean isRerun() {
        return this.rerun;
    }
    
    public boolean isAsyncReporting() {
        return this.asyncReporting;
    }
    
    public void setAsyncReporting(final boolean asyncReporting) {
        this.asyncReporting = asyncReporting;
    }
    
    public boolean isCallbackReportingEnabled() {
        return this.callbackReportingEnabled;
    }
    
    public void setCallbackReportingEnabled(final boolean callbackReportingEnabled) {
        this.callbackReportingEnabled = callbackReportingEnabled;
    }
    
    public void setRerun(final boolean rerun) {
        this.rerun = rerun;
    }
    
    public String getRerunOf() {
        return this.rerunOf;
    }
    
    public void setRerunOf(final String rerunOf) {
        this.rerunOf = rerunOf;
    }
    
    public Integer getIoPoolSize() {
        return this.ioPoolSize;
    }
    
    public void setIoPoolSize(final Integer ioPoolSize) {
        this.ioPoolSize = ioPoolSize;
    }
    
    public Integer getMaxConnectionsPerRoute() {
        return this.maxConnectionsPerRoute;
    }
    
    public void setMaxConnectionsPerRoute(final Integer maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }
    
    public Integer getMaxConnectionsTotal() {
        return this.maxConnectionsTotal;
    }
    
    public void setMaxConnectionsTotal(final Integer maxConnectionsTotal) {
        this.maxConnectionsTotal = maxConnectionsTotal;
    }
    
    public Integer getMaxConnectionTtlMs() {
        return this.maxConnectionTtlMs;
    }
    
    public void setMaxConnectionTtlMs(final Integer maxConnectionTtlMs) {
        this.maxConnectionTtlMs = maxConnectionTtlMs;
    }
    
    public Integer getMaxConnectionIdleTtlMs() {
        return this.maxConnectionIdleTtlMs;
    }
    
    public void setMaxConnectionIdleTtlMs(final Integer maxConnectionIdleTtlMs) {
        this.maxConnectionIdleTtlMs = maxConnectionIdleTtlMs;
    }
    
    public Integer getTransferRetries() {
        return this.transferRetries;
    }
    
    public void setTransferRetries(final Integer transferRetries) {
        this.transferRetries = transferRetries;
    }
    
    public boolean getClientJoin() {
        return this.clientJoin;
    }
    
    public void setClientJoin(final boolean mode) {
        this.clientJoin = mode;
    }
    
    public String getLockFileName() {
        return this.lockFileName;
    }
    
    public void setLockFileName(final String fileName) {
        this.lockFileName = fileName;
    }
    
    public String getSyncFileName() {
        return this.syncFileName;
    }
    
    public void setSyncFileName(final String fileName) {
        this.syncFileName = fileName;
    }
    
    public long getFileWaitTimeout() {
        return this.fileWaitTimeout;
    }
    
    public void setFileWaitTimeout(final long timeout) {
        this.fileWaitTimeout = timeout;
    }
    
    @VisibleForTesting
    Mode parseLaunchMode(final String mode) {
        return Mode.isExists(mode) ? Mode.valueOf(mode.toUpperCase()) : Mode.DEFAULT;
    }
    
    public ListenerParameters clone() {
        ListenerParameters clonedParent;
        try {
            clonedParent = (ListenerParameters)super.clone();
        }
        catch (CloneNotSupportedException exc) {
            clonedParent = new ListenerParameters();
        }
        final ListenerParameters clone = clonedParent;
        final Object obj;
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> {
            if (Modifier.isFinal(f.getModifiers())) {
                return;
            }
            else {
                try {
                    f.set(obj, f.get(this));
                }
                catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                return;
            }
        });
        return clone;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ListenerParameters{");
        sb.append("description='").append(this.description).append('\'');
        sb.append(", apiKey='").append(this.apiKey).append('\'');
        sb.append(", baseUrl='").append(this.baseUrl).append('\'');
        sb.append(", proxyUrl='").append(this.proxyUrl).append('\'');
        sb.append(", projectName='").append(this.projectName).append('\'');
        sb.append(", launchName='").append(this.launchName).append('\'');
        sb.append(", launchRunningMode=").append(this.launchRunningMode);
        sb.append(", attributes=").append(this.attributes);
        sb.append(", enable=").append(this.enable);
        sb.append(", isSkippedAnIssue=").append(this.isSkippedAnIssue);
        sb.append(", batchLogsSize=").append(this.batchLogsSize);
        sb.append(", convertImage=").append(this.convertImage);
        sb.append(", reportingTimeout=").append(this.reportingTimeout);
        sb.append(", keystore='").append(this.keystore).append('\'');
        sb.append(", keystorePassword='").append(this.keystorePassword).append('\'');
        sb.append(", rerun=").append(this.rerun);
        sb.append(", rerunOf='").append(this.rerunOf).append('\'');
        sb.append(", asyncReporting=").append(this.asyncReporting);
        sb.append(", ioPoolSize=").append(this.ioPoolSize);
        sb.append(", callbackReportingEnabled=").append(this.callbackReportingEnabled);
        sb.append(", maxConnectionsPerRoute=").append(this.maxConnectionsPerRoute);
        sb.append(", maxConnectionsTotal=").append(this.maxConnectionsTotal);
        sb.append(", maxConnectionTtlMs=").append(this.maxConnectionTtlMs);
        sb.append(", maxConnectionIdleTtlMs=").append(this.maxConnectionIdleTtlMs);
        sb.append(", transferRetries=").append(this.transferRetries);
        sb.append(", clientJoin=").append(this.clientJoin);
        sb.append(", lockFileName=").append(this.lockFileName);
        sb.append(", syncFileName=").append(this.syncFileName);
        sb.append(", fileWaitTimeout=").append(this.fileWaitTimeout);
        sb.append('}');
        return sb.toString();
    }
    
    static {
        DEFAULT_FILE_WAIT_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(1L);
    }
}
