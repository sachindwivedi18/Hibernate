// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.io;

import java.io.File;
import rp.com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;
import java.io.FilenameFilter;

@Beta
@GwtIncompatible
public final class PatternFilenameFilter implements FilenameFilter
{
    private final Pattern pattern;
    
    public PatternFilenameFilter(final String patternStr) {
        this(Pattern.compile(patternStr));
    }
    
    public PatternFilenameFilter(final Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern);
    }
    
    @Override
    public boolean accept(final File dir, final String fileName) {
        return this.pattern.matcher(fileName).matches();
    }
}
