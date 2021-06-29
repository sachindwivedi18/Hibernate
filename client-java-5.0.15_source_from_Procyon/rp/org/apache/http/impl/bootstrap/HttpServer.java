// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.bootstrap;

import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import rp.org.apache.http.HttpServerConnection;
import javax.net.ssl.SSLServerSocket;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ThreadPoolExecutor;
import rp.org.apache.http.ExceptionLogger;
import rp.org.apache.http.impl.DefaultBHttpServerConnection;
import rp.org.apache.http.HttpConnectionFactory;
import rp.org.apache.http.protocol.HttpService;
import javax.net.ServerSocketFactory;
import rp.org.apache.http.config.SocketConfig;
import java.net.InetAddress;

public class HttpServer
{
    private final int port;
    private final InetAddress ifAddress;
    private final SocketConfig socketConfig;
    private final ServerSocketFactory serverSocketFactory;
    private final HttpService httpService;
    private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private final SSLServerSetupHandler sslSetupHandler;
    private final ExceptionLogger exceptionLogger;
    private final ThreadPoolExecutor listenerExecutorService;
    private final ThreadGroup workerThreads;
    private final WorkerPoolExecutor workerExecutorService;
    private final AtomicReference<Status> status;
    private volatile ServerSocket serverSocket;
    private volatile RequestListener requestListener;
    
    HttpServer(final int port, final InetAddress ifAddress, final SocketConfig socketConfig, final ServerSocketFactory serverSocketFactory, final HttpService httpService, final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory, final SSLServerSetupHandler sslSetupHandler, final ExceptionLogger exceptionLogger) {
        this.port = port;
        this.ifAddress = ifAddress;
        this.socketConfig = socketConfig;
        this.serverSocketFactory = serverSocketFactory;
        this.httpService = httpService;
        this.connectionFactory = connectionFactory;
        this.sslSetupHandler = sslSetupHandler;
        this.exceptionLogger = exceptionLogger;
        this.listenerExecutorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryImpl("HTTP-listener-" + this.port));
        this.workerThreads = new ThreadGroup("HTTP-workers");
        this.workerExecutorService = new WorkerPoolExecutor(0, Integer.MAX_VALUE, 1L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryImpl("HTTP-worker", this.workerThreads));
        this.status = new AtomicReference<Status>(Status.READY);
    }
    
    public InetAddress getInetAddress() {
        final ServerSocket localSocket = this.serverSocket;
        return (localSocket != null) ? localSocket.getInetAddress() : null;
    }
    
    public int getLocalPort() {
        final ServerSocket localSocket = this.serverSocket;
        return (localSocket != null) ? localSocket.getLocalPort() : -1;
    }
    
    public void start() throws IOException {
        if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
            (this.serverSocket = this.serverSocketFactory.createServerSocket(this.port, this.socketConfig.getBacklogSize(), this.ifAddress)).setReuseAddress(this.socketConfig.isSoReuseAddress());
            if (this.socketConfig.getRcvBufSize() > 0) {
                this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
            }
            if (this.sslSetupHandler != null && this.serverSocket instanceof SSLServerSocket) {
                this.sslSetupHandler.initialize((SSLServerSocket)this.serverSocket);
            }
            this.requestListener = new RequestListener(this.socketConfig, this.serverSocket, this.httpService, this.connectionFactory, this.exceptionLogger, this.workerExecutorService);
            this.listenerExecutorService.execute(this.requestListener);
        }
    }
    
    public void stop() {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
            this.listenerExecutorService.shutdown();
            this.workerExecutorService.shutdown();
            final RequestListener local = this.requestListener;
            if (local != null) {
                try {
                    local.terminate();
                }
                catch (IOException ex) {
                    this.exceptionLogger.log(ex);
                }
            }
            this.workerThreads.interrupt();
        }
    }
    
    public void awaitTermination(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        this.workerExecutorService.awaitTermination(timeout, timeUnit);
    }
    
    public void shutdown(final long gracePeriod, final TimeUnit timeUnit) {
        this.stop();
        if (gracePeriod > 0L) {
            try {
                this.awaitTermination(gracePeriod, timeUnit);
            }
            catch (InterruptedException ex2) {
                Thread.currentThread().interrupt();
            }
        }
        final Set<Worker> workers = this.workerExecutorService.getWorkers();
        for (final Worker worker : workers) {
            final HttpServerConnection conn = worker.getConnection();
            try {
                conn.shutdown();
            }
            catch (IOException ex) {
                this.exceptionLogger.log(ex);
            }
        }
    }
    
    enum Status
    {
        READY, 
        ACTIVE, 
        STOPPING;
    }
}
