// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.util.CharArrayBuffer;
import java.io.Serializable;
import rp.org.apache.http.FormattedHeader;

public class BufferedHeader implements FormattedHeader, Cloneable, Serializable
{
    private static final long serialVersionUID = -2768352615787625448L;
    private final String name;
    private final CharArrayBuffer buffer;
    private final int valuePos;
    
    public BufferedHeader(final CharArrayBuffer buffer) throws ParseException {
        Args.notNull(buffer, "Char array buffer");
        final int colon = buffer.indexOf(58);
        if (colon == -1) {
            throw new ParseException("Invalid header: " + buffer.toString());
        }
        final String s = buffer.substringTrimmed(0, colon);
        if (s.isEmpty()) {
            throw new ParseException("Invalid header: " + buffer.toString());
        }
        this.buffer = buffer;
        this.name = s;
        this.valuePos = colon + 1;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getValue() {
        return this.buffer.substringTrimmed(this.valuePos, this.buffer.length());
    }
    
    @Override
    public HeaderElement[] getElements() throws ParseException {
        final ParserCursor cursor = new ParserCursor(0, this.buffer.length());
        cursor.updatePos(this.valuePos);
        return BasicHeaderValueParser.INSTANCE.parseElements(this.buffer, cursor);
    }
    
    @Override
    public int getValuePos() {
        return this.valuePos;
    }
    
    @Override
    public CharArrayBuffer getBuffer() {
        return this.buffer;
    }
    
    @Override
    public String toString() {
        return this.buffer.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
