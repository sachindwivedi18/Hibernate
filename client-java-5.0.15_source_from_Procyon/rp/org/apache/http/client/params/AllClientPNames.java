// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.client.params;

import rp.org.apache.http.conn.params.ConnRoutePNames;
import rp.org.apache.http.conn.params.ConnManagerPNames;
import rp.org.apache.http.conn.params.ConnConnectionPNames;
import rp.org.apache.http.cookie.params.CookieSpecPNames;
import rp.org.apache.http.auth.params.AuthPNames;
import rp.org.apache.http.params.CoreProtocolPNames;
import rp.org.apache.http.params.CoreConnectionPNames;

@Deprecated
public interface AllClientPNames extends CoreConnectionPNames, CoreProtocolPNames, ClientPNames, AuthPNames, CookieSpecPNames, ConnConnectionPNames, ConnManagerPNames, ConnRoutePNames
{
}
