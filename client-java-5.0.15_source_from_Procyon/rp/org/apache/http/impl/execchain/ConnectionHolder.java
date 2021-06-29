// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.execchain;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import rp.org.apache.http.HttpClientConnection;
import rp.org.apache.http.conn.HttpClientConnectionManager;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Closeable;
import rp.org.apache.http.concurrent.Cancellable;
import rp.org.apache.http.conn.ConnectionReleaseTrigger;

@Contract(threading = ThreadingBehavior.SAFE)
class ConnectionHolder implements ConnectionReleaseTrigger, Cancellable, Closeable
{
    private final Log log;
    private final HttpClientConnectionManager manager;
    private final HttpClientConnection managedConn;
    private final AtomicBoolean released;
    private volatile boolean reusable;
    private volatile Object state;
    private volatile long validDuration;
    private volatile TimeUnit timeUnit;
    
    public ConnectionHolder(final Log log, final HttpClientConnectionManager manager, final HttpClientConnection managedConn) {
        this.log = log;
        this.manager = manager;
        this.managedConn = managedConn;
        this.released = new AtomicBoolean(false);
    }
    
    public boolean isReusable() {
        return this.reusable;
    }
    
    public void markReusable() {
        this.reusable = true;
    }
    
    public void markNonReusable() {
        this.reusable = false;
    }
    
    public void setState(final Object state) {
        this.state = state;
    }
    
    public void setValidFor(final long duration, final TimeUnit timeUnit) {
        synchronized (this.managedConn) {
            this.validDuration = duration;
            this.timeUnit = timeUnit;
        }
    }
    
    private void releaseConnection(final boolean reusable) {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                if (reusable) {
                    this.manager.releaseConnection(this.managedConn, this.state, this.validDuration, this.timeUnit);
                }
                else {
                    try {
                        this.managedConn.close();
                        this.log.debug("Connection discarded");
                    }
                    catch (IOException ex) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(ex.getMessage(), ex);
                        }
                    }
                    finally {
                        this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }
    
    @Override
    public void releaseConnection() {
        this.releaseConnection(this.reusable);
    }
    
    @Override
    public void abortConnection() {
        if (this.released.compareAndSet(false, true)) {
            synchronized (this.managedConn) {
                try {
                    this.managedConn.shutdown();
                    this.log.debug("Connection discarded");
                }
                catch (IOException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                }
                finally {
                    this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    @Override
    public boolean cancel() {
        final boolean alreadyReleased = this.released.get();
        this.log.debug("Cancelling request execution");
        this.abortConnection();
        return !alreadyReleased;
    }
    
    public boolean isReleased() {
        return this.released.get();
    }
    
    @Override
    public void close() throws IOException {
        this.releaseConnection(false);
    }
}
