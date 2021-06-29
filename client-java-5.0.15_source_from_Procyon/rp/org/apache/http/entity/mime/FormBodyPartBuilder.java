// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import rp.org.apache.http.entity.ContentType;
import java.util.Iterator;
import java.util.List;
import rp.org.apache.http.entity.mime.content.AbstractContentBody;
import rp.org.apache.http.util.Asserts;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.mime.content.ContentBody;

public class FormBodyPartBuilder
{
    private String name;
    private ContentBody body;
    private final Header header;
    
    public static FormBodyPartBuilder create(final String name, final ContentBody body) {
        return new FormBodyPartBuilder(name, body);
    }
    
    public static FormBodyPartBuilder create() {
        return new FormBodyPartBuilder();
    }
    
    FormBodyPartBuilder(final String name, final ContentBody body) {
        this();
        this.name = name;
        this.body = body;
    }
    
    FormBodyPartBuilder() {
        this.header = new Header();
    }
    
    public FormBodyPartBuilder setName(final String name) {
        this.name = name;
        return this;
    }
    
    public FormBodyPartBuilder setBody(final ContentBody body) {
        this.body = body;
        return this;
    }
    
    public FormBodyPartBuilder addField(final String name, final String value) {
        Args.notNull(name, "Field name");
        this.header.addField(new MinimalField(name, value));
        return this;
    }
    
    public FormBodyPartBuilder setField(final String name, final String value) {
        Args.notNull(name, "Field name");
        this.header.setField(new MinimalField(name, value));
        return this;
    }
    
    public FormBodyPartBuilder removeFields(final String name) {
        Args.notNull(name, "Field name");
        this.header.removeFields(name);
        return this;
    }
    
    public FormBodyPart build() {
        Asserts.notBlank(this.name, "Name");
        Asserts.notNull(this.body, "Content body");
        final Header headerCopy = new Header();
        final List<MinimalField> fields = this.header.getFields();
        for (final MinimalField field : fields) {
            headerCopy.addField(field);
        }
        if (headerCopy.getField("Content-Disposition") == null) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append("form-data; name=\"");
            buffer.append(encodeForHeader(this.name));
            buffer.append("\"");
            if (this.body.getFilename() != null) {
                buffer.append("; filename=\"");
                buffer.append(encodeForHeader(this.body.getFilename()));
                buffer.append("\"");
            }
            headerCopy.addField(new MinimalField("Content-Disposition", buffer.toString()));
        }
        if (headerCopy.getField("Content-Type") == null) {
            ContentType contentType;
            if (this.body instanceof AbstractContentBody) {
                contentType = ((AbstractContentBody)this.body).getContentType();
            }
            else {
                contentType = null;
            }
            if (contentType != null) {
                headerCopy.addField(new MinimalField("Content-Type", contentType.toString()));
            }
            else {
                final StringBuilder buffer2 = new StringBuilder();
                buffer2.append(this.body.getMimeType());
                if (this.body.getCharset() != null) {
                    buffer2.append("; charset=");
                    buffer2.append(this.body.getCharset());
                }
                headerCopy.addField(new MinimalField("Content-Type", buffer2.toString()));
            }
        }
        if (headerCopy.getField("Content-Transfer-Encoding") == null) {
            headerCopy.addField(new MinimalField("Content-Transfer-Encoding", this.body.getTransferEncoding()));
        }
        return new FormBodyPart(this.name, this.body, headerCopy);
    }
    
    private static String encodeForHeader(final String headerName) {
        if (headerName == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headerName.length(); ++i) {
            final char x = headerName.charAt(i);
            if (x == '\"' || x == '\\' || x == '\r') {
                sb.append("\\");
            }
            sb.append(x);
        }
        return sb.toString();
    }
}
