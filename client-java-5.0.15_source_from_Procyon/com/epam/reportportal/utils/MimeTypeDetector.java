// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import org.apache.tika.parser.AutoDetectParser;
import rp.com.google.common.base.Strings;
import rp.com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import java.io.File;
import org.apache.tika.detect.Detector;

public class MimeTypeDetector
{
    private static final Detector detector;
    
    private MimeTypeDetector() {
    }
    
    public static String detect(final File file) throws IOException {
        final Metadata metadata = new Metadata();
        metadata.set("resourceName", file.getName());
        final TikaInputStream is = TikaInputStream.get(file);
        try {
            return detect(is, metadata);
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }
    
    public static String detect(final ByteSource source, final String resourceName) throws IOException {
        final Metadata metadata = new Metadata();
        if (!Strings.isNullOrEmpty(resourceName)) {
            metadata.set("resourceName", resourceName);
        }
        final TikaInputStream is = TikaInputStream.get(source.openBufferedStream());
        try {
            return detect(is, metadata);
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }
    
    public static String detect(final TikaInputStream is, final Metadata metadata) throws IOException {
        return MimeTypeDetector.detector.detect((InputStream)is, metadata).toString();
    }
    
    static {
        detector = new AutoDetectParser().getDetector();
    }
}
