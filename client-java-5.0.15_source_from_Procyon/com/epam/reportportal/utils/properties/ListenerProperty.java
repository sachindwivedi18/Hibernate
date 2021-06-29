// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.properties;

public enum ListenerProperty
{
    BASE_URL("rp.endpoint", true), 
    HTTP_PROXY_URL("rp.http.proxy", false), 
    PROJECT_NAME("rp.project", true), 
    LAUNCH_NAME("rp.launch", true), 
    UUID("rp.uuid", false), 
    API_KEY("rp.api.key", true), 
    BATCH_SIZE_LOGS("rp.batch.size.logs", false), 
    LAUNCH_ATTRIBUTES("rp.attributes", false), 
    DESCRIPTION("rp.description", false), 
    IS_CONVERT_IMAGE("rp.convertimage", false), 
    KEYSTORE_RESOURCE("rp.keystore.resource", false), 
    KEYSTORE_PASSWORD("rp.keystore.password", false), 
    REPORTING_TIMEOUT("rp.reporting.timeout", false), 
    MODE("rp.mode", false), 
    ENABLE("rp.enable", false), 
    RERUN("rp.rerun", false), 
    RERUN_OF("rp.rerun.of", false), 
    ASYNC_REPORTING("rp.reporting.async", false), 
    CALLBACK_REPORTING_ENABLED("rp.reporting.callback", false), 
    SKIPPED_AS_ISSUE("rp.skipped.issue", false), 
    IO_POOL_SIZE("rp.io.pool.size", false), 
    MAX_CONNECTIONS_PER_ROUTE("rp.max.connections.per.route", false), 
    MAX_CONNECTIONS_TOTAL("rp.max.connections.total", false), 
    MAX_CONNECTION_TIME_TO_LIVE("rp.transport.connections.general.ttl.milliseconds", false), 
    MAX_CONNECTION_IDLE_TIME("rp.transport.connections.idle.ttl.milliseconds", false), 
    MAX_TRANSFER_RETRY_COUNT("rp.transport.connections.retry.count", false), 
    CLIENT_JOIN_MODE("rp.client.join", false), 
    LOCK_FILE_NAME("rp.client.join.lock.file.name", false), 
    SYNC_FILE_NAME("rp.client.join.sync.file.name", false), 
    FILE_WAIT_TIMEOUT_MS("rp.client.join.file.wait.timeout.ms", false);
    
    private final String propertyName;
    private final boolean required;
    
    private ListenerProperty(final String propertyName, final boolean required) {
        this.propertyName = propertyName;
        this.required = required;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public boolean isRequired() {
        return this.required;
    }
}
