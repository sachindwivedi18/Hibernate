// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.Header;
import rp.org.apache.http.StatusLine;
import rp.org.apache.http.RequestLine;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.ProtocolVersion;
import rp.org.apache.http.util.CharArrayBuffer;

public interface LineParser
{
    ProtocolVersion parseProtocolVersion(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    boolean hasProtocolVersion(final CharArrayBuffer p0, final ParserCursor p1);
    
    RequestLine parseRequestLine(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    StatusLine parseStatusLine(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    Header parseHeader(final CharArrayBuffer p0) throws ParseException;
}
