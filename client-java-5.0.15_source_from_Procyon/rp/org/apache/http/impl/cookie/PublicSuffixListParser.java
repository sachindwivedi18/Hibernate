// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import java.io.IOException;
import rp.org.apache.http.conn.util.PublicSuffixList;
import java.util.Collection;
import java.io.Reader;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PublicSuffixListParser
{
    private final PublicSuffixFilter filter;
    private final rp.org.apache.http.conn.util.PublicSuffixListParser parser;
    
    PublicSuffixListParser(final PublicSuffixFilter filter) {
        this.filter = filter;
        this.parser = new rp.org.apache.http.conn.util.PublicSuffixListParser();
    }
    
    public void parse(final Reader reader) throws IOException {
        final PublicSuffixList suffixList = this.parser.parse(reader);
        this.filter.setPublicSuffixes(suffixList.getRules());
        this.filter.setExceptions(suffixList.getExceptions());
    }
}
