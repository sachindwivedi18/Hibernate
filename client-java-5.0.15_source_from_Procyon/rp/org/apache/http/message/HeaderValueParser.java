// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.NameValuePair;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.util.CharArrayBuffer;

public interface HeaderValueParser
{
    HeaderElement[] parseElements(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    HeaderElement parseHeaderElement(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    NameValuePair[] parseParameters(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    NameValuePair parseNameValuePair(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
}
