// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.impl.client;

import rp.org.apache.http.config.Lookup;
import rp.org.apache.http.conn.util.PublicSuffixMatcherLoader;
import rp.org.apache.http.impl.cookie.IgnoreSpecProvider;
import rp.org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import rp.org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import rp.org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import rp.org.apache.http.cookie.CookieSpecProvider;
import rp.org.apache.http.config.RegistryBuilder;
import rp.org.apache.http.conn.util.PublicSuffixMatcher;

public final class CookieSpecRegistries
{
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder(final PublicSuffixMatcher publicSuffixMatcher) {
        final CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
        final CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        final CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return (RegistryBuilder<CookieSpecProvider>)RegistryBuilder.create().register("default", (NetscapeDraftSpecProvider)defaultProvider).register("best-match", (NetscapeDraftSpecProvider)defaultProvider).register("compatibility", (NetscapeDraftSpecProvider)defaultProvider).register("standard", (NetscapeDraftSpecProvider)laxStandardProvider).register("standard-strict", (NetscapeDraftSpecProvider)strictStandardProvider).register("netscape", new NetscapeDraftSpecProvider()).register("ignoreCookies", (NetscapeDraftSpecProvider)new IgnoreSpecProvider());
    }
    
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder() {
        return createDefaultBuilder(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault() {
        return createDefault(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault(final PublicSuffixMatcher publicSuffixMatcher) {
        return createDefaultBuilder(publicSuffixMatcher).build();
    }
    
    private CookieSpecRegistries() {
    }
}
