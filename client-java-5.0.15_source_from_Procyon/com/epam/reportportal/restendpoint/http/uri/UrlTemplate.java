// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http.uri;

import rp.com.google.common.base.Joiner;
import rp.com.google.common.net.UrlEscapers;
import rp.com.google.common.base.Preconditions;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class UrlTemplate
{
    private static final Pattern PATH_VARIABLE_PATTERN;
    private final String template;
    private final List<String> pathVariables;
    
    private UrlTemplate(final String template) {
        this.pathVariables = new LinkedList<String>();
        this.template = template;
        this.parsePathVariables();
    }
    
    public static UrlTemplate create(final String template) {
        return new UrlTemplate(template);
    }
    
    public boolean hasPathVariable(final String name) {
        return this.pathVariables.contains(name);
    }
    
    public List<String> getPathVariables() {
        return this.pathVariables;
    }
    
    public Merger merge() {
        return new Merger(this.template);
    }
    
    private void parsePathVariables() {
        final Matcher m = UrlTemplate.PATH_VARIABLE_PATTERN.matcher(this.template);
        while (m.find()) {
            this.pathVariables.add(m.group(1));
        }
    }
    
    static {
        PATH_VARIABLE_PATTERN = Pattern.compile("\\{(.*?)\\}");
    }
    
    public static class Merger
    {
        private StringBuilder template;
        
        private Merger(final String template) {
            this.template = new StringBuilder(template);
        }
        
        public Merger expand(final Map<String, Object> pathParameters) {
            final Matcher m = UrlTemplate.PATH_VARIABLE_PATTERN.matcher(this.template);
            final StringBuffer sb = new StringBuffer();
            while (m.find()) {
                final Object replacement = pathParameters.get(m.group(1));
                Preconditions.checkState(null != replacement, "Unknown path variable: %s", m.group(1));
                m.appendReplacement(sb, Matcher.quoteReplacement(UrlEscapers.urlPathSegmentEscaper().escape(replacement.toString())));
            }
            m.appendTail(sb);
            this.template = new StringBuilder(sb);
            return this;
        }
        
        public Merger appendQueryParameters(final Map<String, ?> parameters) {
            if (null == parameters || parameters.isEmpty()) {
                return this;
            }
            final int lastCharIndex = this.template.length() - 1;
            if ('/' == this.template.charAt(lastCharIndex)) {
                this.template.deleteCharAt(lastCharIndex);
            }
            if (this.template.indexOf("?") == -1) {
                this.template.append("?");
            }
            Joiner.on('&').withKeyValueSeparator("=").appendTo(this.template, (Map<?, ?>)parameters);
            return this;
        }
        
        public String build() {
            return this.template.toString();
        }
    }
}
