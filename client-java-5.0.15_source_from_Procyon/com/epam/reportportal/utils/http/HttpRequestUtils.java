// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.http;

import java.util.Iterator;
import rp.com.google.common.io.ByteSource;
import rp.com.google.common.net.MediaType;
import rp.com.google.common.base.Strings;
import com.epam.reportportal.restendpoint.http.MultiPartRequest;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import java.util.List;

public class HttpRequestUtils
{
    private HttpRequestUtils() {
    }
    
    public static MultiPartRequest buildLogMultiPartRequest(final List<SaveLogRQ> rqs) {
        final MultiPartRequest.Builder builder = new MultiPartRequest.Builder();
        builder.addSerializedPart("json_request_part", rqs);
        for (final SaveLogRQ rq : rqs) {
            final SaveLogRQ.File file = rq.getFile();
            if (null != file) {
                builder.addBinaryPart("binary_part", file.getName(), Strings.isNullOrEmpty(file.getContentType()) ? MediaType.OCTET_STREAM.toString() : file.getContentType(), ByteSource.wrap(file.getContent()));
            }
        }
        return builder.build();
    }
}
