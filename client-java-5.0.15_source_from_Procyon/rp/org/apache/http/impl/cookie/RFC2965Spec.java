// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.cookie;

import rp.org.apache.http.message.BufferedHeader;
import rp.org.apache.http.cookie.ClientCookie;
import rp.org.apache.http.util.CharArrayBuffer;
import rp.org.apache.http.cookie.CookieAttributeHandler;
import java.util.Iterator;
import rp.org.apache.http.cookie.SetCookie;
import java.util.Map;
import java.util.Locale;
import rp.org.apache.http.NameValuePair;
import java.util.HashMap;
import java.util.ArrayList;
import rp.org.apache.http.HeaderElement;
import rp.org.apache.http.util.Args;
import java.util.List;
import rp.org.apache.http.Header;
import rp.org.apache.http.cookie.MalformedCookieException;
import rp.org.apache.http.cookie.CookieRestrictionViolationException;
import rp.org.apache.http.cookie.CookieOrigin;
import rp.org.apache.http.cookie.Cookie;
import rp.org.apache.http.cookie.CommonCookieAttributeHandler;
import rp.org.apache.http.annotation.ThreadingBehavior;
import rp.org.apache.http.annotation.Contract;
import rp.org.apache.http.annotation.Obsolete;

@Obsolete
@Contract(threading = ThreadingBehavior.SAFE)
public class RFC2965Spec extends RFC2109Spec
{
    public RFC2965Spec() {
        this(null, false);
    }
    
    public RFC2965Spec(final String[] datepatterns, final boolean oneHeader) {
        super(oneHeader, new CommonCookieAttributeHandler[] { new RFC2965VersionAttributeHandler(), new BasicPathHandler() {
                @Override
                public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
                    if (!this.match(cookie, origin)) {
                        throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
                    }
                }
            }, new RFC2965DomainAttributeHandler(), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? datepatterns.clone() : RFC2965Spec.DATE_PATTERNS), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler() });
    }
    
    RFC2965Spec(final boolean oneHeader, final CommonCookieAttributeHandler... handlers) {
        super(oneHeader, handlers);
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie2")) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        final HeaderElement[] elems = header.getElements();
        return this.createCookies(elems, adjustEffectiveHost(origin));
    }
    
    @Override
    protected List<Cookie> parse(final HeaderElement[] elems, final CookieOrigin origin) throws MalformedCookieException {
        return this.createCookies(elems, adjustEffectiveHost(origin));
    }
    
    private List<Cookie> createCookies(final HeaderElement[] elems, final CookieOrigin origin) throws MalformedCookieException {
        final List<Cookie> cookies = new ArrayList<Cookie>(elems.length);
        for (final HeaderElement headerelement : elems) {
            final String name = headerelement.getName();
            final String value = headerelement.getValue();
            if (name == null || name.isEmpty()) {
                throw new MalformedCookieException("Cookie name may not be empty");
            }
            final BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
            cookie.setPath(CookieSpecBase.getDefaultPath(origin));
            cookie.setDomain(CookieSpecBase.getDefaultDomain(origin));
            cookie.setPorts(new int[] { origin.getPort() });
            final NameValuePair[] attribs = headerelement.getParameters();
            final Map<String, NameValuePair> attribmap = new HashMap<String, NameValuePair>(attribs.length);
            for (int j = attribs.length - 1; j >= 0; --j) {
                final NameValuePair param = attribs[j];
                attribmap.put(param.getName().toLowerCase(Locale.ROOT), param);
            }
            for (final Map.Entry<String, NameValuePair> entry : attribmap.entrySet()) {
                final NameValuePair attrib = entry.getValue();
                final String s = attrib.getName().toLowerCase(Locale.ROOT);
                cookie.setAttribute(s, attrib.getValue());
                final CookieAttributeHandler handler = this.findAttribHandler(s);
                if (handler != null) {
                    handler.parse(cookie, attrib.getValue());
                }
            }
            cookies.add(cookie);
        }
        return cookies;
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        super.validate(cookie, adjustEffectiveHost(origin));
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        return super.match(cookie, adjustEffectiveHost(origin));
    }
    
    @Override
    protected void formatCookieAsVer(final CharArrayBuffer buffer, final Cookie cookie, final int version) {
        super.formatCookieAsVer(buffer, cookie, version);
        if (cookie instanceof ClientCookie) {
            final String s = ((ClientCookie)cookie).getAttribute("port");
            if (s != null) {
                buffer.append("; $Port");
                buffer.append("=\"");
                if (!s.trim().isEmpty()) {
                    final int[] ports = cookie.getPorts();
                    if (ports != null) {
                        for (int len = ports.length, i = 0; i < len; ++i) {
                            if (i > 0) {
                                buffer.append(",");
                            }
                            buffer.append(Integer.toString(ports[i]));
                        }
                    }
                }
                buffer.append("\"");
            }
        }
    }
    
    private static CookieOrigin adjustEffectiveHost(final CookieOrigin origin) {
        final String host = origin.getHost();
        boolean isLocalHost = true;
        for (int i = 0; i < host.length(); ++i) {
            final char ch = host.charAt(i);
            if (ch == '.' || ch == ':') {
                isLocalHost = false;
                break;
            }
        }
        return isLocalHost ? new CookieOrigin(host + ".local", origin.getPort(), origin.getPath(), origin.isSecure()) : origin;
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public Header getVersionHeader() {
        final CharArrayBuffer buffer = new CharArrayBuffer(40);
        buffer.append("Cookie2");
        buffer.append(": ");
        buffer.append("$Version=");
        buffer.append(Integer.toString(this.getVersion()));
        return new BufferedHeader(buffer);
    }
    
    @Override
    public String toString() {
        return "rfc2965";
    }
}
