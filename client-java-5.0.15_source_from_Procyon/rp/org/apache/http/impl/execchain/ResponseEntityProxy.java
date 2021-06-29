// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.execchain;

import java.net.SocketException;
import java.io.OutputStream;
import rp.org.apache.http.conn.EofSensorInputStream;
import java.io.InputStream;
import java.io.IOException;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.conn.EofSensorWatcher;
import rp.org.apache.http.entity.HttpEntityWrapper;

class ResponseEntityProxy extends HttpEntityWrapper implements EofSensorWatcher
{
    private final ConnectionHolder connHolder;
    
    public static void enchance(final HttpResponse response, final ConnectionHolder connHolder) {
        final HttpEntity entity = response.getEntity();
        if (entity != null && entity.isStreaming() && connHolder != null) {
            response.setEntity(new ResponseEntityProxy(entity, connHolder));
        }
    }
    
    ResponseEntityProxy(final HttpEntity entity, final ConnectionHolder connHolder) {
        super(entity);
        this.connHolder = connHolder;
    }
    
    private void cleanup() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.close();
        }
    }
    
    private void abortConnection() {
        if (this.connHolder != null) {
            this.connHolder.abortConnection();
        }
    }
    
    public void releaseConnection() {
        if (this.connHolder != null) {
            this.connHolder.releaseConnection();
        }
    }
    
    @Override
    public boolean isRepeatable() {
        return false;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }
    
    @Override
    public void consumeContent() throws IOException {
        this.releaseConnection();
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        try {
            if (outStream != null) {
                this.wrappedEntity.writeTo(outStream);
            }
            this.releaseConnection();
        }
        catch (IOException ex) {
            this.abortConnection();
            throw ex;
        }
        catch (RuntimeException ex2) {
            this.abortConnection();
            throw ex2;
        }
        finally {
            this.cleanup();
        }
    }
    
    @Override
    public boolean eofDetected(final InputStream wrapped) throws IOException {
        try {
            if (wrapped != null) {
                wrapped.close();
            }
            this.releaseConnection();
        }
        catch (IOException ex) {
            this.abortConnection();
            throw ex;
        }
        catch (RuntimeException ex2) {
            this.abortConnection();
            throw ex2;
        }
        finally {
            this.cleanup();
        }
        return false;
    }
    
    @Override
    public boolean streamClosed(final InputStream wrapped) throws IOException {
        try {
            final boolean open = this.connHolder != null && !this.connHolder.isReleased();
            try {
                if (wrapped != null) {
                    wrapped.close();
                }
                this.releaseConnection();
            }
            catch (SocketException ex) {
                if (open) {
                    throw ex;
                }
            }
        }
        catch (IOException ex2) {
            this.abortConnection();
            throw ex2;
        }
        catch (RuntimeException ex3) {
            this.abortConnection();
            throw ex3;
        }
        finally {
            this.cleanup();
        }
        return false;
    }
    
    @Override
    public boolean streamAbort(final InputStream wrapped) throws IOException {
        this.cleanup();
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResponseEntityProxy{");
        sb.append(this.wrappedEntity);
        sb.append('}');
        return sb.toString();
    }
}
