// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.files;

import java.io.OutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import rp.com.google.common.net.MediaType;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.imageio.ImageIO;
import java.io.IOException;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import rp.com.google.common.io.ByteSource;
import com.epam.reportportal.message.TypeAwareByteSource;

public class ImageConverter
{
    public static final String IMAGE_TYPE = "image";
    
    public static TypeAwareByteSource convertIfImage(final TypeAwareByteSource content) {
        try {
            return isImage(content) ? convert(content) : content;
        }
        catch (IOException e) {
            throw new InternalReportPortalClientException("Unable to read screenshot file. " + e);
        }
    }
    
    public static TypeAwareByteSource convert(final ByteSource source) throws IOException {
        final BufferedImage image = ImageIO.read(source.openBufferedStream());
        final BufferedImage blackAndWhiteImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 10);
        final Graphics2D graphics2D = (Graphics2D)blackAndWhiteImage.getGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return convertToInputStream(blackAndWhiteImage);
    }
    
    public static boolean isImage(final TypeAwareByteSource source) {
        return isImage(source.getMediaType());
    }
    
    public static boolean isImage(final MediaType contentType) {
        return contentType.type().equalsIgnoreCase("image");
    }
    
    public static boolean isImage(final String contentType) {
        return isImage(MediaType.parse(contentType));
    }
    
    private static TypeAwareByteSource convertToInputStream(final BufferedImage image) {
        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", byteOutputStream);
        }
        catch (IOException e) {
            throw new InternalReportPortalClientException("Unable to transform file to byte array.", e);
        }
        return new TypeAwareByteSource(ByteSource.wrap(byteOutputStream.toByteArray()), MediaType.PNG.toString());
    }
}
