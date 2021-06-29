// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import java.util.Optional;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import java.util.concurrent.TimeUnit;
import com.epam.reportportal.utils.Waiter;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.io.FileNotFoundException;
import com.epam.reportportal.listeners.ListenerParameters;
import java.nio.channels.FileLock;
import java.io.RandomAccessFile;
import org.apache.commons.lang3.tuple.Pair;
import java.io.File;
import org.slf4j.Logger;

public class LockFile implements LaunchIdLock
{
    private static final Logger LOGGER;
    public static final String LOCK_FILE_CHARSET = "US-ASCII";
    private static final String LINE_SEPARATOR;
    private static final float MAX_WAIT_TIME_DISCREPANCY = 0.1f;
    private final File lockFile;
    private final File syncFile;
    private final long fileWaitTimeout;
    private volatile String lockUuid;
    private volatile Pair<RandomAccessFile, FileLock> mainLock;
    
    public LockFile(final ListenerParameters parameters) {
        this.lockFile = new File(parameters.getLockFileName());
        this.syncFile = new File(parameters.getSyncFileName());
        this.fileWaitTimeout = parameters.getFileWaitTimeout();
    }
    
    private Pair<RandomAccessFile, FileLock> obtainLock(final File file) {
        final String filePath = file.getPath();
        RandomAccessFile lockAccess;
        try {
            lockAccess = new RandomAccessFile(file, "rwd");
        }
        catch (FileNotFoundException e) {
            LockFile.LOGGER.debug("Unable to open '{}' file: {}", new Object[] { filePath, e.getLocalizedMessage(), e });
            return null;
        }
        try {
            final FileLock lock = lockAccess.getChannel().tryLock();
            if (lock == null) {
                closeAccess(lockAccess);
                return null;
            }
            return (Pair<RandomAccessFile, FileLock>)Pair.of((Object)lockAccess, (Object)lock);
        }
        catch (OverlappingFileLockException e2) {
            LockFile.LOGGER.debug("Lock already acquired on '{}' file: {}", new Object[] { filePath, e2.getLocalizedMessage(), e2 });
        }
        catch (ClosedChannelException e3) {
            LockFile.LOGGER.warn("Channel was already closed on '{}' file: {}", new Object[] { filePath, e3.getLocalizedMessage(), e3 });
        }
        catch (IOException e4) {
            LockFile.LOGGER.warn("Unexpected I/O exception while obtaining mainLock on '{}' file: {}", new Object[] { filePath, e4.getLocalizedMessage(), e4 });
        }
        closeAccess(lockAccess);
        return null;
    }
    
    private Pair<RandomAccessFile, FileLock> waitForLock(final File file) {
        return new Waiter("Wait for file '" + file.getPath() + "' lock").duration(this.fileWaitTimeout, TimeUnit.MILLISECONDS).applyRandomDiscrepancy(0.1f).till(() -> this.obtainLock(file));
    }
    
    private static void releaseLock(final FileLock lock) {
        try {
            lock.release();
        }
        catch (ClosedChannelException e) {
            LockFile.LOGGER.warn("Channel was already closed for file mainLock: {}", (Object)e.getLocalizedMessage(), (Object)e);
        }
        catch (IOException e2) {
            LockFile.LOGGER.warn("Unexpected I/O exception while releasing file mainLock: {}", (Object)e2.getLocalizedMessage(), (Object)e2);
        }
    }
    
    private static void closeAccess(final RandomAccessFile access) {
        try {
            access.close();
        }
        catch (IOException e) {
            LockFile.LOGGER.warn("Unexpected I/O exception while closing file: {}", (Object)e.getLocalizedMessage(), (Object)e);
        }
    }
    
    private static String readLaunchUuid(final RandomAccessFile access) throws IOException {
        return access.readLine();
    }
    
    private static void writeString(final RandomAccessFile access, final String text) throws IOException {
        access.write(text.getBytes("US-ASCII"));
    }
    
    private static void writeLine(final RandomAccessFile access, final String text) throws IOException {
        writeString(access, text + LockFile.LINE_SEPARATOR);
    }
    
    private static void closeIo(final Pair<RandomAccessFile, FileLock> io) {
        releaseLock((FileLock)io.getRight());
        closeAccess((RandomAccessFile)io.getLeft());
    }
    
    private <T> T executeBlockingOperation(final IoOperation<T> operation, final File file) {
        final Pair<RandomAccessFile, FileLock> fileIo;
        return new Waiter("Wait for a blocking operation on file '" + file.getPath() + "'").duration(this.fileWaitTimeout, TimeUnit.MILLISECONDS).applyRandomDiscrepancy(0.1f).till(() -> {
            fileIo = this.obtainLock(file);
            if (fileIo != null) {
                try {
                    return operation.execute(fileIo);
                }
                catch (IOException e) {
                    LockFile.LOGGER.error("Unable to read/write a file after obtaining mainLock: " + e.getMessage(), (Throwable)e);
                }
                finally {
                    closeIo(fileIo);
                }
            }
            return null;
        });
    }
    
    private void rewriteWith(final RandomAccessFile access, final String content) throws IOException {
        access.setLength(content.length());
        writeLine(access, content);
    }
    
    void reset() {
        if (this.mainLock != null) {
            closeIo(this.mainLock);
            this.mainLock = null;
        }
        this.lockUuid = null;
    }
    
    private void writeLaunchUuid(final Pair<RandomAccessFile, FileLock> syncIo) {
        try {
            this.rewriteWith((RandomAccessFile)syncIo.getLeft(), this.lockUuid);
            this.rewriteWith((RandomAccessFile)this.mainLock.getLeft(), this.lockUuid);
        }
        catch (IOException e) {
            final String error = "Unable to read/write a file after obtaining lock: " + e.getMessage();
            LockFile.LOGGER.warn(error, (Throwable)e);
            this.reset();
            throw new InternalReportPortalClientException(error, e);
        }
    }
    
    private static void appendUuid(final RandomAccessFile access, final String uuid) throws IOException {
        access.seek(access.length());
        writeLine(access, uuid);
    }
    
    private void writeInstanceUuid(final String instanceUuid) {
        final IoOperation<Boolean> uuidRead = fileIo -> {
            appendUuid((RandomAccessFile)fileIo.getKey(), instanceUuid);
            return Boolean.TRUE;
        };
        this.executeBlockingOperation(uuidRead, this.syncFile);
    }
    
    private String obtainLaunch(final String instanceUuid) {
        final RandomAccessFile access;
        final String uuid;
        final IoOperation<String> uuidRead = fileIo -> {
            access = (RandomAccessFile)fileIo.getKey();
            uuid = readLaunchUuid(access);
            appendUuid(access, instanceUuid);
            return Optional.ofNullable(uuid).orElse(instanceUuid);
        };
        return this.executeBlockingOperation(uuidRead, this.syncFile);
    }
    
    @Override
    public String obtainLaunchUuid(@Nonnull final String uuid) {
        Objects.requireNonNull(uuid);
        if (this.mainLock != null) {
            if (!uuid.equals(this.lockUuid)) {
                this.writeInstanceUuid(uuid);
            }
            return this.lockUuid;
        }
        final Pair<RandomAccessFile, FileLock> syncLock = this.obtainLock(this.syncFile);
        if (syncLock != null) {
            try {
                if (this.mainLock == null) {
                    final Pair<RandomAccessFile, FileLock> lock = this.obtainLock(this.lockFile);
                    if (lock != null) {
                        this.mainLock = lock;
                        this.lockUuid = uuid;
                        this.writeLaunchUuid(syncLock);
                        return uuid;
                    }
                }
            }
            finally {
                closeIo(syncLock);
            }
        }
        return this.obtainLaunch(uuid);
    }
    
    @Override
    public void finishInstanceUuid(final String uuid) {
        if (uuid == null) {
            return;
        }
        final List<String> uuidList;
        final RandomAccessFile fileAccess;
        boolean needUpdate;
        String line;
        final Object o;
        String trimmedLine;
        String uuidNl;
        long newLength;
        final Iterator<String> iterator;
        String uuid2;
        final IoOperation<Boolean> uuidRemove = fileIo -> {
            uuidList = new ArrayList<String>();
            fileAccess = (RandomAccessFile)fileIo.getKey();
            needUpdate = false;
            while (true) {
                line = fileAccess.readLine();
                if (o != null) {
                    trimmedLine = line.trim();
                    if (uuid.equals(trimmedLine)) {
                        needUpdate = true;
                    }
                    else {
                        uuidList.add(trimmedLine);
                    }
                }
                else {
                    break;
                }
            }
            if (!needUpdate) {
                return Boolean.valueOf(false);
            }
            else {
                uuidNl = uuid + LockFile.LINE_SEPARATOR;
                newLength = fileAccess.length() - uuidNl.length();
                if (newLength > 0L) {
                    fileAccess.setLength(newLength);
                    fileAccess.seek(0L);
                    uuidList.iterator();
                    while (iterator.hasNext()) {
                        uuid2 = iterator.next();
                        writeLine(fileAccess, uuid2);
                    }
                    return Boolean.valueOf(false);
                }
                else {
                    ((RandomAccessFile)fileIo.getKey()).setLength(0L);
                    return Boolean.valueOf(true);
                }
            }
        };
        final Boolean isLast = this.executeBlockingOperation(uuidRemove, this.syncFile);
        if (isLast != null && isLast) {
            this.syncFile.delete();
        }
        if (this.mainLock != null && this.lockUuid.equals(uuid)) {
            this.reset();
            this.lockFile.delete();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)LockFile.class);
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
    
    private interface IoOperation<T>
    {
        T execute(final Pair<RandomAccessFile, FileLock> p0) throws IOException;
    }
}
