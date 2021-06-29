// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import rp.com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import com.epam.reportportal.annotations.attribute.MultiValueAttribute;
import com.epam.reportportal.annotations.attribute.MultiKeyAttribute;
import com.epam.reportportal.annotations.attribute.AttributeValue;
import com.epam.reportportal.annotations.attribute.Attribute;
import java.util.Collection;
import com.epam.reportportal.annotations.attribute.Attributes;
import rp.com.google.common.collect.Sets;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import java.util.Set;

public class AttributeParser
{
    public static final String ATTRIBUTES_SPLITTER = ";";
    public static final String KEY_VALUE_SPLITTER = ":";
    
    public static Set<ItemAttributesRQ> parseAsSet(final String rawAttributes) {
        if (null == rawAttributes) {
            return (Set<ItemAttributesRQ>)Sets.newHashSet();
        }
        final Set<ItemAttributesRQ> attributes = (Set<ItemAttributesRQ>)Sets.newHashSet();
        final String[] split;
        final String[] attributesSplit = split = rawAttributes.trim().split(";");
        for (final String s : split) {
            final ItemAttributesRQ itemAttributeResource = splitKeyValue(s);
            if (itemAttributeResource != null) {
                attributes.add(itemAttributeResource);
            }
        }
        return attributes;
    }
    
    public static ItemAttributesRQ splitKeyValue(final String attribute) {
        if (null == attribute || attribute.trim().isEmpty()) {
            return null;
        }
        final String[] keyValue = attribute.split(":");
        if (keyValue.length == 1) {
            return new ItemAttributesRQ((String)null, keyValue[0].trim());
        }
        if (keyValue.length == 2) {
            String key = keyValue[0].trim();
            if (key.isEmpty()) {
                key = null;
            }
            return new ItemAttributesRQ(key, keyValue[1].trim());
        }
        return null;
    }
    
    public static Set<ItemAttributesRQ> retrieveAttributes(final Attributes attributesAnnotation) {
        final Set<ItemAttributesRQ> itemAttributes = (Set<ItemAttributesRQ>)Sets.newLinkedHashSet();
        for (final Attribute attribute : attributesAnnotation.attributes()) {
            if (!attribute.value().trim().isEmpty()) {
                itemAttributes.add(createItemAttribute(attribute.key(), attribute.value()));
            }
        }
        for (final AttributeValue attributeValue : attributesAnnotation.attributeValues()) {
            if (!attributeValue.value().trim().isEmpty()) {
                itemAttributes.add(createItemAttribute(null, attributeValue.value()));
            }
        }
        for (final MultiKeyAttribute attribute2 : attributesAnnotation.multiKeyAttributes()) {
            itemAttributes.addAll(createItemAttributes(attribute2.keys(), attribute2.value()));
        }
        for (final MultiValueAttribute attribute3 : attributesAnnotation.multiValueAttributes()) {
            itemAttributes.addAll(createItemAttributes(attribute3.isNullKey() ? null : attribute3.key(), attribute3.values()));
        }
        return itemAttributes;
    }
    
    private static List<ItemAttributesRQ> createItemAttributes(final String[] keys, final String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (keys == null || keys.length < 1) {
            return Collections.singletonList(createItemAttribute(null, value));
        }
        final List<ItemAttributesRQ> itemAttributes = (List<ItemAttributesRQ>)Lists.newArrayListWithExpectedSize(keys.length);
        for (final String key : keys) {
            itemAttributes.add(createItemAttribute(key, value));
        }
        return itemAttributes;
    }
    
    private static List<ItemAttributesRQ> createItemAttributes(final String key, final String[] values) {
        if (values != null && values.length > 0) {
            final List<ItemAttributesRQ> attributes = (List<ItemAttributesRQ>)Lists.newArrayListWithExpectedSize(values.length);
            for (final String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    attributes.add(createItemAttribute(key, value));
                }
            }
            return attributes;
        }
        return Collections.emptyList();
    }
    
    private static ItemAttributesRQ createItemAttribute(final String key, final String value) {
        return new ItemAttributesRQ(key, value);
    }
}
