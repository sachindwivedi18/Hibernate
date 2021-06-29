// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.message;

import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.ParseException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import java.io.Serializable;
import rp.org.apache.http.Header;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicHeader implements Header, Cloneable, Serializable
{
    private static final HeaderElement[] EMPTY_HEADER_ELEMENTS;
    private static final long serialVersionUID = -5427236326487562174L;
    private final String name;
    private final String value;
    
    public BasicHeader(final String name, final String value) {
        this.name = Args.notNull(name, "Name");
        this.value = value;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public HeaderElement[] getElements() throws ParseException {
        if (this.getValue() != null) {
            return BasicHeaderValueParser.parseElements(this.getValue(), null);
        }
        return BasicHeader.EMPTY_HEADER_ELEMENTS;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return BasicLineFormatter.INSTANCE.formatHeader(null, this).toString();
    }
    
    static {
        EMPTY_HEADER_ELEMENTS = new HeaderElement[0];
    }
}
