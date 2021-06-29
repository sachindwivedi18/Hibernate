// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.files;

import com.epam.reportportal.utils.MimeTypeDetector;
import rp.com.google.common.io.ByteSource;
import java.io.FileNotFoundException;
import com.epam.reportportal.message.TypeAwareByteSource;
import java.io.FileInputStream;
import java.io.File;
import java.nio.channels.ReadableByteChannel;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import java.io.InputStream;

public class Utils
{
    private static final int KILOBYTE = 8;
    private static final int READ_BUFFER = 80;
    
    private Utils() {
    }
    
    public static String readInputStreamToString(@Nonnull final InputStream is) throws IOException {
        final byte[] bytes = readInputStreamToBytes(is);
        if (bytes.length <= 0) {
            return "";
        }
        try {
            return new String(bytes, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    public static byte[] readInputStreamToBytes(@Nonnull final InputStream is) throws IOException {
        return readInputStreamToBytes(is, 80);
    }
    
    public static byte[] readInputStreamToBytes(@Nonnull final InputStream is, final int bufferSize) throws IOException {
        final ReadableByteChannel channel = Channels.newChannel(is);
        final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        while ((read = channel.read(buffer)) > 0) {
            baos.write(buffer.array(), 0, read);
            buffer.clear();
        }
        return baos.toByteArray();
    }
    
    public static byte[] readFileToBytes(@Nonnull final File file) throws IOException {
        return readInputStreamToBytes(new FileInputStream(file));
    }
    
    public static TypeAwareByteSource getFile(@Nonnull final File file) throws IOException {
        byte[] data;
        if (file.exists() && file.isFile()) {
            data = readFileToBytes(file);
        }
        else {
            final String path = file.getPath();
            final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (resource == null) {
                throw new FileNotFoundException("Unable to locate file of path: " + file.getPath());
            }
            data = readInputStreamToBytes(resource);
        }
        final String name = file.getName();
        final ByteSource byteSource = ByteSource.wrap(data);
        return new TypeAwareByteSource(byteSource, MimeTypeDetector.detect(byteSource, name));
    }
}
