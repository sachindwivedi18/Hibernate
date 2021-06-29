// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Serializable;
import rp.org.apache.http.StatusLine;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicStatusLine implements StatusLine, Cloneable, Serializable
{
    private static final long serialVersionUID = -2443303766890459269L;
    private final ProtocolVersion protoVersion;
    private final int statusCode;
    private final String reasonPhrase;
    
    public BasicStatusLine(final ProtocolVersion version, final int statusCode, final String reasonPhrase) {
        this.protoVersion = Args.notNull(version, "Version");
        this.statusCode = Args.notNegative(statusCode, "Status code");
        this.reasonPhrase = reasonPhrase;
    }
    
    @Override
    public int getStatusCode() {
        return this.statusCode;
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.protoVersion;
    }
    
    @Override
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
    
    @Override
    public String toString() {
        return BasicLineFormatter.INSTANCE.formatStatusLine(null, this).toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
