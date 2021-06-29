// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.properties;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.Collection;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import java.util.Set;
import org.slf4j.Logger;

public class SystemAttributesExtractor
{
    private static final Logger LOGGER;
    public static final String ATTRIBUTE_VALUE_SEPARATOR = "|";
    
    private SystemAttributesExtractor() {
    }
    
    public static Set<ItemAttributesRQ> extract(final String resource, final ClassLoader loader) {
        final Set<ItemAttributesRQ> attributes = getInternalAttributes();
        final Properties properties = loadProperties(resource, loader);
        attributes.addAll(getExternalAttributes(properties, (PropertyHolder[])DefaultProperties.values()));
        return attributes;
    }
    
    public static Set<ItemAttributesRQ> extract(final String resource, final ClassLoader loader, final PropertyHolder... propertyHolders) {
        final Properties properties = loadProperties(resource, loader);
        return getExternalAttributes(properties, propertyHolders);
    }
    
    private static Properties loadProperties(final String resource, final ClassLoader loader) {
        final Properties properties = new Properties();
        InputStreamReader inputStreamReader;
        final Properties properties2;
        final Throwable t2;
        Optional.ofNullable(loader).flatMap(l -> Optional.ofNullable(resource).flatMap(res -> Optional.ofNullable(l.getResourceAsStream(res)))).ifPresent(resStream -> {
            try {
                inputStreamReader = new InputStreamReader(resStream, StandardCharsets.UTF_8);
                try {
                    properties2.load(inputStreamReader);
                }
                catch (Throwable t) {
                    throw t;
                }
                finally {
                    if (inputStreamReader != null) {
                        if (t2 != null) {
                            try {
                                inputStreamReader.close();
                            }
                            catch (Throwable exception) {
                                t2.addSuppressed(exception);
                            }
                        }
                        else {
                            inputStreamReader.close();
                        }
                    }
                }
            }
            catch (IOException e) {
                SystemAttributesExtractor.LOGGER.warn("Unable to load system properties file");
            }
            return;
        });
        return properties;
    }
    
    public static Set<ItemAttributesRQ> extract(final Path path) {
        final Set<ItemAttributesRQ> attributes = getInternalAttributes();
        final Properties properties = loadProperties(path);
        attributes.addAll(getExternalAttributes(properties, (PropertyHolder[])DefaultProperties.values()));
        return attributes;
    }
    
    public static Set<ItemAttributesRQ> extract(final Path path, final PropertyHolder... propertyHolders) {
        final Properties properties = loadProperties(path);
        return getExternalAttributes(properties, propertyHolders);
    }
    
    private static Properties loadProperties(final Path path) {
        final Properties properties = new Properties();
        final File file;
        final InputStreamReader inputStreamReader2;
        InputStreamReader inputStreamReader;
        final Properties properties2;
        final Throwable t2;
        Optional.ofNullable(path).ifPresent(p -> {
            file = p.toFile();
            if (file.exists()) {
                try {
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                    inputStreamReader = inputStreamReader2;
                    try {
                        properties2.load(inputStreamReader);
                    }
                    catch (Throwable t) {
                        throw t;
                    }
                    finally {
                        if (inputStreamReader != null) {
                            if (t2 != null) {
                                try {
                                    inputStreamReader.close();
                                }
                                catch (Throwable exception) {
                                    t2.addSuppressed(exception);
                                }
                            }
                            else {
                                inputStreamReader.close();
                            }
                        }
                    }
                }
                catch (IOException e) {
                    SystemAttributesExtractor.LOGGER.warn("Unable to load system properties file");
                }
            }
            return;
        });
        return properties;
    }
    
    private static Set<ItemAttributesRQ> getInternalAttributes() {
        return Arrays.stream(DefaultProperties.values()).filter(DefaultProperties::isInternal).map(defaultProperty -> convert(defaultProperty.getName(), defaultProperty.getPropertyKeys())).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).collect((Collector<? super Object, ?, Set<ItemAttributesRQ>>)Collectors.toSet());
    }
    
    private static Set<ItemAttributesRQ> getExternalAttributes(final Properties externalAttributes, final PropertyHolder... propertyHolders) {
        return Arrays.stream(propertyHolders).filter(defaultProperties -> !defaultProperties.isInternal()).map(defaultProperty -> convert(defaultProperty.getName(), externalAttributes, defaultProperty.getPropertyKeys())).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).collect((Collector<? super Object, ?, Set<ItemAttributesRQ>>)Collectors.toSet());
    }
    
    private static Optional<ItemAttributesRQ> convert(final String attributeKey, final Properties properties, final String... propertyKeys) {
        final Function<String, Optional<String>> propertyExtractor = getPropertyExtractor(properties);
        return extractAttribute(propertyExtractor, attributeKey, propertyKeys);
    }
    
    private static Optional<ItemAttributesRQ> convert(final String attributeKey, final String... propertyKeys) {
        final Function<String, Optional<String>> propertyExtractor = getPropertyExtractor();
        return extractAttribute(propertyExtractor, attributeKey, propertyKeys);
    }
    
    private static Function<String, Optional<String>> getPropertyExtractor(final Properties properties) {
        return (Function<String, Optional<String>>)(key -> Optional.ofNullable(properties.getProperty(key)));
    }
    
    private static Function<String, Optional<String>> getPropertyExtractor() {
        return (Function<String, Optional<String>>)(key -> Optional.ofNullable(System.getProperty(key)));
    }
    
    private static Optional<ItemAttributesRQ> extractAttribute(final Function<String, Optional<String>> propertyExtractor, final String attributeKey, final String... propertyKeys) {
        final List<String> values = Arrays.stream(propertyKeys).map((Function<? super String, ?>)propertyExtractor).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        if (!values.isEmpty()) {
            return Optional.of(new ItemAttributesRQ(attributeKey, StringUtils.join((Iterable)values, "|"), true));
        }
        return Optional.empty();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SystemAttributesExtractor.class);
    }
}
