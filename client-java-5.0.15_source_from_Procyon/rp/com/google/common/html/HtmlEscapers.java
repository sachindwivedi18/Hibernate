// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.html;

import rp.com.google.common.escape.Escapers;
import rp.com.google.common.escape.Escaper;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class HtmlEscapers
{
    private static final Escaper HTML_ESCAPER;
    
    public static Escaper htmlEscaper() {
        return HtmlEscapers.HTML_ESCAPER;
    }
    
    private HtmlEscapers() {
    }
    
    static {
        HTML_ESCAPER = Escapers.builder().addEscape('\"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").build();
    }
}
