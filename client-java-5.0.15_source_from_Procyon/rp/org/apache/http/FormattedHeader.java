// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

import rp.org.apache.http.util.CharArrayBuffer;

public interface FormattedHeader extends Header
{
    CharArrayBuffer getBuffer();
    
    int getValuePos();
}
