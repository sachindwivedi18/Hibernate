// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.message;

import java.net.URL;
import rp.com.google.common.io.Resources;
import com.epam.reportportal.utils.MimeTypeDetector;
import rp.com.google.common.io.ByteSource;
import rp.com.google.common.io.BaseEncoding;
import com.epam.reportportal.utils.files.Utils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import rp.com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class HashMarkSeparatedMessageParser implements MessageParser
{
    private static final int CHUNKS_COUNT = 4;
    private static final Pattern CHUNK_DELIMITER;
    
    @Override
    public ReportPortalMessage parse(final String message) throws IOException {
        final Matcher m = HashMarkSeparatedMessageParser.CHUNK_DELIMITER.matcher(Pattern.quote(message));
        int chunkIdx = 0;
        final List<String> split = new ArrayList<String>(4);
        int prevRegion = 0;
        while (m.find()) {
            final String chunk = message.substring(prevRegion, m.start() - 2);
            prevRegion = m.start() - 1;
            if (!chunk.isEmpty()) {
                split.add(chunk);
            }
            if (++chunkIdx >= 3) {
                break;
            }
        }
        split.add(message.substring(prevRegion));
        if (4 != split.size()) {
            throw new RuntimeException("Incorrect message format. Chunks: " + Joiner.on("\n").join(split) + "\n count: " + split.size());
        }
        return new ReportPortalMessage(MessageType.fromString(split.get(1)).toByteSource(split.get(2)), split.get(3));
    }
    
    @Override
    public boolean supports(final String message) {
        return message.startsWith("RP_MESSAGE");
    }
    
    static {
        CHUNK_DELIMITER = Pattern.compile("#");
    }
    
    private enum MessageType
    {
        FILE {
            @Override
            public TypeAwareByteSource toByteSource(final String data) throws IOException {
                final File file = new File(data);
                if (!file.exists()) {
                    return null;
                }
                return Utils.getFile(file);
            }
        }, 
        BASE64 {
            @Override
            public TypeAwareByteSource toByteSource(final String data) throws IOException {
                if (data.contains(":")) {
                    final String[] parts = data.split(":");
                    final String type = parts[1];
                    return new TypeAwareByteSource(ByteSource.wrap(BaseEncoding.base64().decode(parts[0])), type);
                }
                final ByteSource source = ByteSource.wrap(BaseEncoding.base64().decode(data));
                return new TypeAwareByteSource(source, MimeTypeDetector.detect(source, null));
            }
        }, 
        RESOURCE {
            @Override
            public TypeAwareByteSource toByteSource(final String resourceName) throws IOException {
                final URL resource = Resources.getResource(resourceName);
                if (null == resource) {
                    return null;
                }
                final ByteSource source = Resources.asByteSource(resource);
                return new TypeAwareByteSource(source, MimeTypeDetector.detect(source, resourceName));
            }
        };
        
        public abstract TypeAwareByteSource toByteSource(final String p0) throws IOException;
        
        public static MessageType fromString(final String messageType) {
            return valueOf(messageType);
        }
    }
}
