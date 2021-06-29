// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import java.util.Map;
import java.lang.reflect.Type;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import io.reactivex.Maybe;
import java.io.Closeable;

public interface RestEndpoint extends Closeable
{
     <RQ, RS> Maybe<Response<RS>> post(final String p0, final RQ p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<RS> postFor(final String p0, final RQ p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<Response<RS>> post(final String p0, final RQ p1, final Type p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<RS> postFor(final String p0, final RQ p1, final Type p2) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> post(final String p0, final MultiPartRequest p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RS> Maybe<RS> postFor(final String p0, final MultiPartRequest p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<Response<RS>> put(final String p0, final RQ p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<RS> putFor(final String p0, final RQ p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<Response<RS>> put(final String p0, final RQ p1, final Type p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<RS> putFor(final String p0, final RQ p1, final Type p2) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> delete(final String p0, final Class<RS> p1) throws RestEndpointIOException;
    
     <RS> Maybe<RS> deleteFor(final String p0, final Class<RS> p1) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> get(final String p0, final Class<RS> p1) throws RestEndpointIOException;
    
     <RS> Maybe<RS> getFor(final String p0, final Class<RS> p1) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> get(final String p0, final Type p1) throws RestEndpointIOException;
    
     <RS> Maybe<RS> getFor(final String p0, final Type p1) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> get(final String p0, final Map<String, String> p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RS> Maybe<RS> getFor(final String p0, final Map<String, String> p1, final Class<RS> p2) throws RestEndpointIOException;
    
     <RS> Maybe<Response<RS>> get(final String p0, final Map<String, String> p1, final Type p2) throws RestEndpointIOException;
    
     <RS> Maybe<RS> getFor(final String p0, final Map<String, String> p1, final Type p2) throws RestEndpointIOException;
    
     <RQ, RS> Maybe<Response<RS>> executeRequest(final RestCommand<RQ, RS> p0) throws RestEndpointIOException;
}
