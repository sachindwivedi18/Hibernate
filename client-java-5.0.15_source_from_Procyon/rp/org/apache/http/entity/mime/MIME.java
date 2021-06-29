// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import rp.org.apache.http.Consts;
import java.nio.charset.Charset;

public final class MIME
{
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TRANSFER_ENC = "Content-Transfer-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String ENC_8BIT = "8bit";
    public static final String ENC_BINARY = "binary";
    public static final Charset DEFAULT_CHARSET;
    public static final Charset UTF8_CHARSET;
    
    static {
        DEFAULT_CHARSET = Consts.ASCII;
        UTF8_CHARSET = Consts.UTF_8;
    }
}
