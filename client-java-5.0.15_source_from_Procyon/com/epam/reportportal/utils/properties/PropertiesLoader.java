// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils.properties;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.HashMap;
import rp.com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import rp.com.google.common.base.Suppliers;
import java.io.IOException;
import com.epam.reportportal.exception.InternalReportPortalClientException;
import java.util.Properties;
import java.util.function.Supplier;
import java.nio.charset.Charset;

public class PropertiesLoader
{
    public static final String INNER_PATH = "reportportal.properties";
    public static final String PATH = "./reportportal.properties";
    public static final Charset STANDARD_CHARSET;
    private final Supplier<Properties> propertiesSupplier;
    
    public static PropertiesLoader load() {
        return new PropertiesLoader(() -> {
            try {
                return loadProperties("reportportal.properties");
            }
            catch (IOException e) {
                throw new InternalReportPortalClientException("Unable to load properties", e);
            }
        });
    }
    
    public static PropertiesLoader load(final String resource) {
        return new PropertiesLoader(() -> {
            try {
                return loadProperties(resource);
            }
            catch (IOException e) {
                throw new InternalReportPortalClientException("Unable to load properties", e);
            }
        });
    }
    
    private PropertiesLoader(final Supplier<Properties> propertiesSupplier) {
        this.propertiesSupplier = (Supplier<Properties>)Suppliers.memoize(propertiesSupplier::get);
    }
    
    public String getProperty(final String propertyName) {
        return this.propertiesSupplier.get().getProperty(propertyName);
    }
    
    public String getProperty(final ListenerProperty propertyName, final String defaultValue) {
        final String value = this.propertiesSupplier.get().getProperty(propertyName.getPropertyName());
        return (value != null) ? value : defaultValue;
    }
    
    public boolean getPropertyAsBoolean(final ListenerProperty propertyName, final boolean defaultValue) {
        final String value = this.propertiesSupplier.get().getProperty(propertyName.getPropertyName());
        return (null != value) ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public int getPropertyAsInt(final ListenerProperty propertyName, final int defaultValue) {
        final String value = this.propertiesSupplier.get().getProperty(propertyName.getPropertyName());
        return (null != value) ? Integer.parseInt(value) : defaultValue;
    }
    
    public String getProperty(final ListenerProperty propertyName) {
        return this.propertiesSupplier.get().getProperty(propertyName.getPropertyName());
    }
    
    public Properties getProperties() {
        return this.propertiesSupplier.get();
    }
    
    public void overrideWith(final Properties overrides) {
        overrideWith(this.propertiesSupplier.get(), overrides);
    }
    
    private static Properties loadProperties(final String resource) throws IOException {
        final Properties props = new Properties();
        final Optional<URL> propertyFile = getResource(resource);
        if (propertyFile.isPresent()) {
            try (final InputStream is = propertyFile.get().openStream()) {
                props.load(new InputStreamReader(is, PropertiesLoader.STANDARD_CHARSET));
            }
        }
        overrideWith(props, System.getProperties());
        overrideWith(props, System.getenv());
        return props;
    }
    
    @Deprecated
    private static Properties loadFromFile() throws IOException {
        final Properties props = new Properties();
        final File propertiesFile = new File("./reportportal.properties");
        try (final InputStream is = propertiesFile.exists() ? new FileInputStream(propertiesFile) : PropertiesLoader.class.getResourceAsStream("reportportal.properties");
             final InputStreamReader isr = new InputStreamReader(is, PropertiesLoader.STANDARD_CHARSET)) {
            props.load(isr);
        }
        return props;
    }
    
    public void validate() {
        validateProperties(this.getProperties());
    }
    
    private static void validateProperties(final Properties properties) {
        for (final ListenerProperty listenerProperty : ListenerProperty.values()) {
            if (listenerProperty.isRequired() && properties.getProperty(listenerProperty.getPropertyName()) == null) {
                throw new IllegalArgumentException("Property '" + listenerProperty.getPropertyName() + "' should not be null.");
            }
        }
    }
    
    @VisibleForTesting
    static void overrideWith(final Properties source, final Map<String, String> overrides) {
        final Map<String, String> overridesNormalized = normalizeOverrides(overrides);
        for (final ListenerProperty listenerProperty : ListenerProperty.values()) {
            if (overridesNormalized.get(listenerProperty.getPropertyName()) != null) {
                source.setProperty(listenerProperty.getPropertyName(), overridesNormalized.get(listenerProperty.getPropertyName()));
            }
        }
    }
    
    private static Map<String, String> normalizeOverrides(final Map<String, String> overrides) {
        final Map<String, String> normalizedSet = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : overrides.entrySet()) {
            if (entry.getKey() != null) {
                final String key = entry.getKey().toLowerCase().replace("_", ".");
                normalizedSet.put(key, entry.getValue());
            }
        }
        return normalizedSet;
    }
    
    @VisibleForTesting
    static void overrideWith(final Properties source, final Properties overrides) {
        overrideWith(source, (Map<String, String>)overrides);
    }
    
    private static Optional<URL> getResource(final String resourceName) {
        final ClassLoader loader = Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(PropertiesLoader.class.getClassLoader());
        return Optional.ofNullable(loader.getResource(resourceName));
    }
    
    static {
        STANDARD_CHARSET = StandardCharsets.UTF_8;
    }
}
