// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import java.io.InputStream;
import java.io.Closeable;
import com.epam.reportportal.restendpoint.http.IOUtils;
import rp.com.google.common.io.Resources;
import java.security.KeyStore;

public class SslUtils
{
    public static KeyStore loadKeyStore(final String keyStore, final String password) {
        InputStream is = null;
        try {
            is = Resources.asByteSource(Resources.getResource(keyStore)).openStream();
            final KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(is, password.toCharArray());
            return trustStore;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load trust store", e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }
}
