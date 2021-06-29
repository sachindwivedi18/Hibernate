// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.markdown;

import rp.com.google.common.base.Strings;

public class MarkdownUtils
{
    public static final String MARKDOWN_MODE = "!!!MARKDOWN_MODE!!!";
    private static final char NEW_LINE = '\n';
    
    public static String asMarkdown(final String message) {
        return "!!!MARKDOWN_MODE!!!".concat(message);
    }
    
    public static String asCode(final String language, final String script) {
        return "!!!MARKDOWN_MODE!!!" + "```" + Strings.nullToEmpty(language) + '\n' + script + '\n' + "```";
    }
}
