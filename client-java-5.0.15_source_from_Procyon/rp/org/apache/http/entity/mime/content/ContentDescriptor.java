// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime.content;

public interface ContentDescriptor
{
    String getMimeType();
    
    String getMediaType();
    
    String getSubType();
    
    String getCharset();
    
    String getTransferEncoding();
    
    long getContentLength();
}
