// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.auth;

import java.util.Locale;
import rp.org.apache.http.auth.AuthenticationException;
import rp.org.apache.http.protocol.HttpContext;
import rp.org.apache.http.HttpRequest;
import rp.org.apache.http.auth.Credentials;
import rp.org.apache.http.protocol.HTTP;
import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.FormattedHeader;
import rp.org.apache.http.auth.MalformedChallengeException;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.Header;
import rp.org.apache.http.auth.ChallengeState;
import rp.org.apache.http.auth.ContextAwareAuthScheme;

public abstract class AuthSchemeBase implements ContextAwareAuthScheme
{
    protected ChallengeState challengeState;
    
    @Deprecated
    public AuthSchemeBase(final ChallengeState challengeState) {
        this.challengeState = challengeState;
    }
    
    public AuthSchemeBase() {
    }
    
    @Override
    public void processChallenge(final Header header) throws MalformedChallengeException {
        Args.notNull(header, "Header");
        final String authheader = header.getName();
        if (authheader.equalsIgnoreCase("WWW-Authenticate")) {
            this.challengeState = ChallengeState.TARGET;
        }
        else {
            if (!authheader.equalsIgnoreCase("Proxy-Authenticate")) {
                throw new MalformedChallengeException("Unexpected header name: " + authheader);
            }
            this.challengeState = ChallengeState.PROXY;
        }
        CharArrayBuffer buffer;
        int pos;
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader)header).getBuffer();
            pos = ((FormattedHeader)header).getValuePos();
        }
        else {
            final String s = header.getValue();
            if (s == null) {
                throw new MalformedChallengeException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            pos = 0;
        }
        while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
            ++pos;
        }
        final int beginIndex = pos;
        while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
            ++pos;
        }
        final int endIndex = pos;
        final String s2 = buffer.substring(beginIndex, endIndex);
        if (!s2.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid scheme identifier: " + s2);
        }
        this.parseChallenge(buffer, pos, buffer.length());
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return this.authenticate(credentials, request);
    }
    
    protected abstract void parseChallenge(final CharArrayBuffer p0, final int p1, final int p2) throws MalformedChallengeException;
    
    public boolean isProxy() {
        return this.challengeState != null && this.challengeState == ChallengeState.PROXY;
    }
    
    public ChallengeState getChallengeState() {
        return this.challengeState;
    }
    
    @Override
    public String toString() {
        final String name = this.getSchemeName();
        return (name != null) ? name.toUpperCase(Locale.ROOT) : super.toString();
    }
}
