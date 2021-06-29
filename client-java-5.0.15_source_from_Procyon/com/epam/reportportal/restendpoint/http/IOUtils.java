// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.KeyStore;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import rp.com.google.common.base.Strings;
import java.io.IOException;
import java.io.Closeable;

public final class IOUtils
{
    private IOUtils() {
    }
    
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public static boolean isValidUrl(final String url) {
        try {
            if (Strings.isNullOrEmpty(url)) {
                return false;
            }
            new URL(url);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }
    
    public static KeyStore loadKeyStore(final InputStream keyStore, final String password) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        try {
            final KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(keyStore, password.toCharArray());
            return trustStore;
        }
        finally {
            closeQuietly(keyStore);
        }
    }
}
