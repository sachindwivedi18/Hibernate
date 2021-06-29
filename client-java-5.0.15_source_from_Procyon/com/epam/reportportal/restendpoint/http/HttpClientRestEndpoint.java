// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.restendpoint.http;

import rp.org.apache.http.util.EntityUtils;
import java.io.InputStream;
import rp.com.google.common.base.Suppliers;
import rp.com.google.common.base.Supplier;
import java.io.IOException;
import java.io.Closeable;
import rp.org.apache.http.impl.client.CloseableHttpClient;
import rp.org.apache.http.Header;
import rp.org.apache.http.HttpResponse;
import rp.com.google.common.collect.Multimap;
import rp.com.google.common.io.ByteSource;
import rp.com.google.common.collect.ImmutableMultimap;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import rp.com.google.common.io.Closer;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import rp.org.apache.http.client.utils.URIBuilder;
import java.nio.charset.Charset;
import rp.com.google.common.net.MediaType;
import java.net.URISyntaxException;
import rp.org.apache.http.client.methods.HttpPatch;
import java.util.Map;
import rp.org.apache.http.client.methods.HttpGet;
import rp.org.apache.http.client.methods.HttpDelete;
import rp.org.apache.http.client.methods.HttpPut;
import java.util.Iterator;
import rp.org.apache.http.entity.mime.content.InputStreamBody;
import rp.org.apache.http.entity.mime.content.ContentBody;
import rp.org.apache.http.entity.mime.content.StringBody;
import rp.org.apache.http.entity.ContentType;
import rp.com.google.common.base.Charsets;
import rp.org.apache.http.entity.mime.MultipartEntityBuilder;
import java.net.URI;
import java.lang.reflect.Type;
import io.reactivex.functions.Function;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import rp.org.apache.http.client.methods.HttpUriRequest;
import rp.org.apache.http.HttpEntity;
import rp.org.apache.http.entity.ByteArrayEntity;
import rp.org.apache.http.client.methods.HttpPost;
import io.reactivex.Maybe;
import java.util.concurrent.Executor;
import io.reactivex.schedulers.Schedulers;
import rp.com.google.common.base.Strings;
import com.epam.reportportal.restendpoint.serializer.VoidSerializer;
import rp.com.google.common.collect.ImmutableList;
import rp.com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import rp.com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import io.reactivex.Scheduler;
import rp.org.apache.http.client.HttpClient;
import com.epam.reportportal.restendpoint.serializer.Serializer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClientRestEndpoint implements RestEndpoint
{
    private static final int DEFAULT_POOL_SIZE = 100;
    private static final long POOL_DRAIN_TIMEOUT = 1L;
    private static final TimeUnit POOL_DRAIN_TIME_UNIT;
    private final List<Serializer> serializers;
    private final String baseUrl;
    private final ErrorHandler errorHandler;
    private final HttpClient httpClient;
    private final Scheduler scheduler;
    private final ExecutorService executor;
    
    public HttpClientRestEndpoint(final HttpClient httpClient, final List<Serializer> serializers, final ErrorHandler errorHandler) {
        this(httpClient, serializers, errorHandler, null);
    }
    
    public HttpClientRestEndpoint(final HttpClient httpClient, final List<Serializer> serializers, final ErrorHandler errorHandler, final String baseUrl) {
        this(httpClient, serializers, errorHandler, baseUrl, Executors.newFixedThreadPool(100, new ThreadFactoryBuilder().setNameFormat("rp-io-%s").build()));
    }
    
    public HttpClientRestEndpoint(final HttpClient httpClient, final List<Serializer> serializers, final ErrorHandler errorHandler, final String baseUrl, final ExecutorService executorService) {
        this.executor = executorService;
        Preconditions.checkArgument(null != serializers && !serializers.isEmpty(), (Object)"There is no any serializer provided");
        this.serializers = (List<Serializer>)ImmutableList.builder().addAll((Iterable<? extends VoidSerializer>)serializers).add(new VoidSerializer()).build();
        if (!Strings.isNullOrEmpty(baseUrl)) {
            Preconditions.checkArgument(IOUtils.isValidUrl(baseUrl), "'%s' is not valid URL", baseUrl);
        }
        this.baseUrl = baseUrl;
        this.scheduler = Schedulers.from((Executor)executorService);
        this.errorHandler = ((errorHandler == null) ? new DefaultErrorHandler() : errorHandler);
        this.httpClient = httpClient;
    }
    
    @Override
    public final <RQ, RS> Maybe<Response<RS>> post(final String resource, final RQ rq, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpPost post = new HttpPost(this.spliceUrl(resource));
        final Serializer serializer = this.getSupportedSerializer(rq);
        final ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), this.toContentType(serializer.getMimeType()));
        post.setEntity(httpEntity);
        return this.executeInternal(post, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    @Override
    public final <RQ, RS> Maybe<RS> postFor(final String resource, final RQ rq, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.post(resource, (Object)rq, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RQ, RS> Maybe<Response<RS>> post(final String resource, final RQ rq, final Type type) throws RestEndpointIOException {
        final HttpPost post = new HttpPost(this.spliceUrl(resource));
        final Serializer serializer = this.getSupportedSerializer(rq);
        final ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), this.toContentType(serializer.getMimeType()));
        post.setEntity(httpEntity);
        return this.executeInternal(post, new TypeConverterCallback<RS>(this.serializers, type));
    }
    
    @Override
    public final <RQ, RS> Maybe<RS> postFor(final String resource, final RQ rq, final Type type) throws RestEndpointIOException {
        final Maybe<Response<RS>> post = (Maybe<Response<RS>>)this.post(resource, (Object)rq, type);
        return (Maybe<RS>)post.flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> post(final String resource, final MultiPartRequest request, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpPost post = this.buildMultipartRequest(this.spliceUrl(resource), request);
        return this.executeInternal(post, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    private HttpPost buildMultipartRequest(final URI uri, final MultiPartRequest request) throws RestEndpointIOException {
        final HttpPost post = new HttpPost(uri);
        try {
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (final MultiPartRequest.MultiPartSerialized<?> serializedPart : request.getSerializedRQs()) {
                final Serializer serializer = this.getSupportedSerializer(serializedPart);
                builder.addPart(serializedPart.getPartName(), new StringBody(new String(serializer.serialize(serializedPart.getRequest()), Charsets.UTF_8), ContentType.parse(serializer.getMimeType().toString())));
            }
            for (final MultiPartRequest.MultiPartBinary partBinaty : request.getBinaryRQs()) {
                builder.addPart(partBinaty.getPartName(), new InputStreamBody(partBinaty.getData().openBufferedStream(), ContentType.parse(partBinaty.getContentType()), partBinaty.getFilename()));
            }
            post.setEntity(builder.build());
        }
        catch (Exception e) {
            throw new RestEndpointIOException("Unable to build post multipart request", e);
        }
        return post;
    }
    
    @Override
    public final <RS> Maybe<RS> postFor(final String resource, final MultiPartRequest request, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.post(resource, request, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RQ, RS> Maybe<Response<RS>> put(final String resource, final RQ rq, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpPut put = new HttpPut(this.spliceUrl(resource));
        final Serializer serializer = this.getSupportedSerializer(rq);
        final ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), this.toContentType(serializer.getMimeType()));
        put.setEntity(httpEntity);
        return this.executeInternal(put, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    @Override
    public final <RQ, RS> Maybe<RS> putFor(final String resource, final RQ rq, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.put(resource, (Object)rq, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RQ, RS> Maybe<Response<RS>> put(final String resource, final RQ rq, final Type type) throws RestEndpointIOException {
        final HttpPut put = new HttpPut(this.spliceUrl(resource));
        final Serializer serializer = this.getSupportedSerializer(rq);
        final ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), this.toContentType(serializer.getMimeType()));
        put.setEntity(httpEntity);
        return this.executeInternal(put, new TypeConverterCallback<RS>(this.serializers, type));
    }
    
    @Override
    public final <RQ, RS> Maybe<RS> putFor(final String resource, final RQ rq, final Type type) throws RestEndpointIOException {
        final Maybe<Response<RS>> rs = (Maybe<Response<RS>>)this.put(resource, (Object)rq, type);
        return (Maybe<RS>)rs.flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> delete(final String resource, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpDelete delete = new HttpDelete(this.spliceUrl(resource));
        return this.executeInternal(delete, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    @Override
    public final <RS> Maybe<RS> deleteFor(final String resource, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.delete(resource, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> get(final String resource, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpGet get = new HttpGet(this.spliceUrl(resource));
        return this.executeInternal(get, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    @Override
    public final <RS> Maybe<RS> getFor(final String resource, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.get(resource, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> get(final String resource, final Type type) throws RestEndpointIOException {
        final HttpGet get = new HttpGet(this.spliceUrl(resource));
        return this.executeInternal(get, new TypeConverterCallback<RS>(this.serializers, type));
    }
    
    @Override
    public final <RS> Maybe<RS> getFor(final String resource, final Type type) throws RestEndpointIOException {
        final Maybe<Response<RS>> rs = (Maybe<Response<RS>>)this.get(resource, type);
        return (Maybe<RS>)rs.flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> get(final String resource, final Map<String, String> parameters, final Class<RS> clazz) throws RestEndpointIOException {
        final HttpGet get = new HttpGet(this.spliceUrl(resource, parameters));
        return this.executeInternal(get, new ClassConverterCallback<RS>(this.serializers, clazz));
    }
    
    @Override
    public final <RS> Maybe<RS> getFor(final String resource, final Map<String, String> parameters, final Class<RS> clazz) throws RestEndpointIOException {
        return (Maybe<RS>)this.get(resource, parameters, (Class<Object>)clazz).flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RS> Maybe<Response<RS>> get(final String resource, final Map<String, String> parameters, final Type type) throws RestEndpointIOException {
        final HttpGet get = new HttpGet(this.spliceUrl(resource, parameters));
        return this.executeInternal(get, new TypeConverterCallback<RS>(this.serializers, type));
    }
    
    @Override
    public final <RS> Maybe<RS> getFor(final String resource, final Map<String, String> parameters, final Type type) throws RestEndpointIOException {
        final Maybe<Response<RS>> rs = (Maybe<Response<RS>>)this.get(resource, parameters, type);
        return (Maybe<RS>)rs.flatMap((Function)new BodyTransformer());
    }
    
    @Override
    public final <RQ, RS> Maybe<Response<RS>> executeRequest(final RestCommand<RQ, RS> command) throws RestEndpointIOException {
        final URI uri = this.spliceUrl(command.getUri());
        HttpUriRequest rq = null;
        switch (command.getHttpMethod()) {
            case GET: {
                rq = new HttpGet(uri);
                break;
            }
            case POST: {
                if (command.isMultipart()) {
                    final MultiPartRequest rqData = (MultiPartRequest)command.getRequest();
                    rq = this.buildMultipartRequest(uri, rqData);
                    break;
                }
                final Serializer serializer = this.getSupportedSerializer(command.getRequest());
                rq = new HttpPost(uri);
                ((HttpPost)rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), this.toContentType(serializer.getMimeType())));
                break;
            }
            case PUT: {
                final Serializer serializer = this.getSupportedSerializer(command.getRequest());
                rq = new HttpPut(uri);
                ((HttpPut)rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), this.toContentType(serializer.getMimeType())));
                break;
            }
            case DELETE: {
                rq = new HttpDelete(uri);
                break;
            }
            case PATCH: {
                final Serializer serializer = this.getSupportedSerializer(command.getRequest());
                rq = new HttpPatch(uri);
                ((HttpPatch)rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), ContentType.create(serializer.getMimeType().toString())));
                break;
            }
            default: {
                throw new IllegalArgumentException("Method '" + command.getHttpMethod() + "' is unsupported");
            }
        }
        return this.executeInternal(rq, new TypeConverterCallback<RS>(this.serializers, command.getResponseType()));
    }
    
    private URI spliceUrl(final String resource) throws RestEndpointIOException {
        try {
            return Strings.isNullOrEmpty(this.baseUrl) ? new URI(resource) : new URI(this.baseUrl.concat(resource));
        }
        catch (URISyntaxException e) {
            throw new RestEndpointIOException("Unable to builder URL with base url '" + this.baseUrl + "' and resouce '" + resource + "'", e);
        }
    }
    
    private ContentType toContentType(final MediaType contentType) {
        return ContentType.create(contentType.withoutParameters().toString(), contentType.charset().or(Charsets.UTF_8));
    }
    
    final URI spliceUrl(final String resource, final Map<String, String> parameters) throws RestEndpointIOException {
        try {
            URIBuilder builder;
            if (!Strings.isNullOrEmpty(this.baseUrl)) {
                builder = new URIBuilder(this.baseUrl);
                builder.setPath(builder.getPath() + resource);
            }
            else {
                builder = new URIBuilder(resource);
            }
            for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
                builder.addParameter(parameter.getKey(), parameter.getValue());
            }
            return builder.build();
        }
        catch (URISyntaxException e) {
            throw new RestEndpointIOException("Unable to builder URL with base url '" + this.baseUrl + "' and resouce '" + resource + "'", e);
        }
    }
    
    private Serializer getSupportedSerializer(final Object o) throws SerializerException {
        for (final Serializer s : this.serializers) {
            if (s.canWrite(o)) {
                return s;
            }
        }
        throw new SerializerException("Unable to find serializer for object with type '" + o.getClass() + "'");
    }
    
    private <RS> Maybe<Response<RS>> executeInternal(final HttpUriRequest rq, final HttpEntityCallback<RS> callback) {
        if (this.executor.isShutdown()) {
            throw new IllegalStateException("Executor pool shut down");
        }
        final Closer closer = Closer.create();
        return (Maybe<Response<RS>>)Maybe.create((MaybeOnSubscribe)new MaybeOnSubscribe<Response<RS>>() {
            public void subscribe(final MaybeEmitter<Response<RS>> emitter) throws Exception {
                try {
                    final HttpResponse response = HttpClientRestEndpoint.this.httpClient.execute(rq);
                    final LazyByteSource bodySupplier = new LazyByteSource(response.getEntity());
                    closer.register(bodySupplier);
                    final Header[] allHeaders = response.getAllHeaders();
                    final ImmutableMultimap.Builder<String, String> headersBuilder = ImmutableMultimap.builder();
                    for (final Header header : allHeaders) {
                        headersBuilder.put(header.getName().toLowerCase(), (null == header.getValue()) ? "" : header.getValue());
                    }
                    final Response<ByteSource> rs1 = new Response<ByteSource>(rq.getURI(), HttpMethod.valueOf(rq.getMethod()), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), headersBuilder.build(), bodySupplier);
                    if (HttpClientRestEndpoint.this.errorHandler.hasError(rs1)) {
                        HttpClientRestEndpoint.this.errorHandler.handle(rs1);
                    }
                    final MediaType contentType = (null == response.getEntity().getContentType()) ? MediaType.ANY_TYPE : MediaType.parse(response.getEntity().getContentType().getValue());
                    if (!HttpClientRestEndpoint.this.executor.isShutdown()) {
                        emitter.onSuccess((Object)new Response(rs1.getUri(), rs1.getHttpMethod(), rs1.getStatus(), rs1.getReason(), rs1.getHeaders(), callback.callback(contentType, bodySupplier.read())));
                    }
                }
                catch (Throwable error) {
                    if (!HttpClientRestEndpoint.this.executor.isShutdown()) {
                        emitter.onError(error);
                    }
                }
                finally {
                    closer.close();
                }
            }
        }).subscribeOn(this.scheduler);
    }
    
    @Override
    public void close() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(1L, HttpClientRestEndpoint.POOL_DRAIN_TIME_UNIT);
        }
        catch (InterruptedException ex) {}
        if (this.httpClient instanceof CloseableHttpClient) {
            IOUtils.closeQuietly((Closeable)this.httpClient);
        }
    }
    
    static {
        POOL_DRAIN_TIME_UNIT = TimeUnit.MINUTES;
    }
    
    private abstract static class HttpEntityCallback<RS>
    {
        final List<Serializer> serializers;
        
        HttpEntityCallback(final List<Serializer> serializers) {
            this.serializers = serializers;
        }
        
        public abstract RS callback(final MediaType p0, final byte[] p1) throws IOException;
    }
    
    private static class TypeConverterCallback<RS> extends HttpEntityCallback<RS>
    {
        private final Type type;
        
        TypeConverterCallback(final List<Serializer> serializers, final Type type) {
            super(serializers);
            this.type = type;
        }
        
        @Override
        public RS callback(final MediaType contentType, final byte[] body) throws IOException {
            return this.getSupported(contentType, this.type).deserialize(body, this.type);
        }
        
        Serializer getSupported(final MediaType contentType, final Type resultType) throws SerializerException {
            for (final Serializer s : this.serializers) {
                if (s.canRead(contentType, resultType)) {
                    return s;
                }
            }
            throw new SerializerException("Conversion media type '" + contentType + "' to type '" + resultType + "' is not supported");
        }
    }
    
    private static class ClassConverterCallback<RS> extends HttpEntityCallback<RS>
    {
        private final Class<RS> clazz;
        
        ClassConverterCallback(final List<Serializer> serializers, final Class<RS> clazz) {
            super(serializers);
            this.clazz = clazz;
        }
        
        @Override
        public RS callback(final MediaType contentType, final byte[] body) throws IOException {
            return this.getSupported(contentType, this.clazz).deserialize(body, this.clazz);
        }
        
        private Serializer getSupported(final MediaType contentType, final Class<?> resultType) throws SerializerException {
            for (final Serializer s : this.serializers) {
                if (s.canRead(contentType, resultType)) {
                    return s;
                }
            }
            throw new SerializerException("Conversion media type '" + contentType + "' to type '" + resultType + "' is not supported");
        }
    }
    
    public static final class BodyTransformer<T> implements Function<Response<T>, Maybe<T>>
    {
        public Maybe<T> apply(final Response<T> input) {
            final T body = input.getBody();
            return (Maybe<T>)((null == body) ? Maybe.empty() : Maybe.just((Object)body));
        }
    }
    
    private static class LazyByteSource extends ByteSource implements Closeable
    {
        private final HttpEntity httpEntity;
        private final Supplier<ByteSource> supplier;
        
        private LazyByteSource(final HttpEntity httpEntity) {
            this.httpEntity = httpEntity;
            this.supplier = Suppliers.memoize((Supplier<ByteSource>)new Supplier<ByteSource>() {
                @Override
                public ByteSource get() {
                    return ByteSource.wrap(LazyByteSource.this.readEntity(httpEntity));
                }
            });
        }
        
        @Override
        public InputStream openStream() throws IOException {
            return this.supplier.get().openStream();
        }
        
        private byte[] readEntity(final HttpEntity entity) {
            try {
                return EntityUtils.toByteArray(entity);
            }
            catch (IOException e) {
                throw new RestEndpointIOException("Unable to read body from error", e);
            }
            finally {
                EntityUtils.consumeQuietly(entity);
            }
        }
        
        @Override
        public void close() throws IOException {
            EntityUtils.consumeQuietly(this.httpEntity);
        }
    }
}
