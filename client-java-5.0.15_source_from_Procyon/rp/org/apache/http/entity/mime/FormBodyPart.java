// 
// Decompiled by Procyon v0.5.36
// 

package rp.org.apache.http.entity.mime;

import rp.org.apache.http.entity.ContentType;
import rp.org.apache.http.entity.mime.content.AbstractContentBody;
import rp.org.apache.http.util.Args;
import rp.org.apache.http.entity.mime.content.ContentBody;

public class FormBodyPart
{
    private final String name;
    private final Header header;
    private final ContentBody body;
    
    FormBodyPart(final String name, final ContentBody body, final Header header) {
        Args.notNull(name, "Name");
        Args.notNull(body, "Body");
        this.name = name;
        this.body = body;
        this.header = ((header != null) ? header : new Header());
    }
    
    @Deprecated
    public FormBodyPart(final String name, final ContentBody body) {
        Args.notNull(name, "Name");
        Args.notNull(body, "Body");
        this.name = name;
        this.body = body;
        this.header = new Header();
        this.generateContentDisp(body);
        this.generateContentType(body);
        this.generateTransferEncoding(body);
    }
    
    public String getName() {
        return this.name;
    }
    
    public ContentBody getBody() {
        return this.body;
    }
    
    public Header getHeader() {
        return this.header;
    }
    
    public void addField(final String name, final String value) {
        Args.notNull(name, "Field name");
        this.header.addField(new MinimalField(name, value));
    }
    
    @Deprecated
    protected void generateContentDisp(final ContentBody body) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("form-data; name=\"");
        buffer.append(this.getName());
        buffer.append("\"");
        if (body.getFilename() != null) {
            buffer.append("; filename=\"");
            buffer.append(body.getFilename());
            buffer.append("\"");
        }
        this.addField("Content-Disposition", buffer.toString());
    }
    
    @Deprecated
    protected void generateContentType(final ContentBody body) {
        ContentType contentType;
        if (body instanceof AbstractContentBody) {
            contentType = ((AbstractContentBody)body).getContentType();
        }
        else {
            contentType = null;
        }
        if (contentType != null) {
            this.addField("Content-Type", contentType.toString());
        }
        else {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(body.getMimeType());
            if (body.getCharset() != null) {
                buffer.append("; charset=");
                buffer.append(body.getCharset());
            }
            this.addField("Content-Type", buffer.toString());
        }
    }
    
    @Deprecated
    protected void generateTransferEncoding(final ContentBody body) {
        this.addField("Content-Transfer-Encoding", body.getTransferEncoding());
    }
}
