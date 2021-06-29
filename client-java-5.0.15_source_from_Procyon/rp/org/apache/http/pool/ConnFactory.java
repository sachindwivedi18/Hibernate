// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C>
{
    C create(final T p0) throws IOException;
}
