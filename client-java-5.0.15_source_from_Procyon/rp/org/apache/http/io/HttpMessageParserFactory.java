// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.io;

import rp.org.apache.http.config.MessageConstraints;
import rp.org.apache.http.HttpMessage;

public interface HttpMessageParserFactory<T extends HttpMessage>
{
    HttpMessageParser<T> create(final SessionInputBuffer p0, final MessageConstraints p1);
}
