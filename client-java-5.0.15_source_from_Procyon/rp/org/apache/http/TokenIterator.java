// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http;

import java.util.Iterator;

public interface TokenIterator extends Iterator<Object>
{
    boolean hasNext();
    
    String nextToken();
}
