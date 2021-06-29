// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.io;

import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;
import java.nio.file.FileSystemException;

@Beta
@GwtIncompatible
public final class InsecureRecursiveDeleteException extends FileSystemException
{
    public InsecureRecursiveDeleteException(final String file) {
        super(file, null, "unable to guarantee security of recursive delete");
    }
}
