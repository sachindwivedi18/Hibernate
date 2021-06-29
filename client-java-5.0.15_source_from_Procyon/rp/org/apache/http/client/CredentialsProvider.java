// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client;

import rp.org.apache.http.auth.Credentials;
import rp.org.apache.http.auth.AuthScope;

public interface CredentialsProvider
{
    void setCredentials(final AuthScope p0, final Credentials p1);
    
    Credentials getCredentials(final AuthScope p0);
    
    void clear();
}
