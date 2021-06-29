// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.client.AuthCache;
import rp.org.apache.http.auth.Credentials;
import rp.org.apache.http.auth.AuthScheme;
import rp.org.apache.http.auth.AuthScope;
import java.util.Locale;
import rp.org.apache.http.auth.AuthenticationException;
import rp.org.apache.http.client.CredentialsProvider;
import java.util.LinkedList;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.auth.AuthOption;
import java.util.Queue;
import rp.org.apache.http.auth.MalformedChallengeException;
import rp.org.apache.http.Header;
import java.util.Map;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpResponse;
import rp.org.apache.http.HttpHost;
import rp.org.apache.commons.logging.LogFactory;
import rp.org.apache.http.client.AuthenticationHandler;
import rp.org.apache.commons.logging.Log;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.client.AuthenticationStrategy;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
class AuthenticationStrategyAdaptor implements AuthenticationStrategy
{
    private final Log log;
    private final AuthenticationHandler handler;
    
    public AuthenticationStrategyAdaptor(final AuthenticationHandler handler) {
        this.log = LogFactory.getLog(this.getClass());
        this.handler = handler;
    }
    
    @Override
    public boolean isAuthenticationRequested(final HttpHost authhost, final HttpResponse response, final HttpContext context) {
        return this.handler.isAuthenticationRequested(response, context);
    }
    
    @Override
    public Map<String, Header> getChallenges(final HttpHost authhost, final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        return this.handler.getChallenges(response, context);
    }
    
    @Override
    public Queue<AuthOption> select(final Map<String, Header> challenges, final HttpHost authhost, final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        Args.notNull(challenges, "Map of auth challenges");
        Args.notNull(authhost, "Host");
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        final Queue<AuthOption> options = new LinkedList<AuthOption>();
        final CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
            return options;
        }
        AuthScheme authScheme;
        try {
            authScheme = this.handler.selectScheme(challenges, response, context);
        }
        catch (AuthenticationException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(ex.getMessage(), ex);
            }
            return options;
        }
        final String id = authScheme.getSchemeName();
        final Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
        authScheme.processChallenge(challenge);
        final AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
        final Credentials credentials = credsProvider.getCredentials(authScope);
        if (credentials != null) {
            options.add(new AuthOption(authScheme, credentials));
        }
        return options;
    }
    
    @Override
    public void authSucceeded(final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
        if (this.isCachable(authScheme)) {
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute("http.auth.auth-cache", authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.put(authhost, authScheme);
        }
    }
    
    @Override
    public void authFailed(final HttpHost authhost, final AuthScheme authScheme, final HttpContext context) {
        final AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
        if (authCache == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
        }
        authCache.remove(authhost);
    }
    
    private boolean isCachable(final AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        final String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase("Basic");
    }
    
    public AuthenticationHandler getHandler() {
        return this.handler;
    }
}
