// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.auth;

import java.security.Principal;

public interface Credentials
{
    Principal getUserPrincipal();
    
    String getPassword();
}
