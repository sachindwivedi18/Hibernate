// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.utils;

import java.lang.reflect.Array;
import java.util.Iterator;
import com.epam.reportportal.utils.reflect.Accessible;
import com.epam.reportportal.annotations.StepTemplateConfig;

public class StepTemplateUtils
{
    private static final String NULL_VALUE = "NULL";
    
    private StepTemplateUtils() {
    }
    
    public static String retrieveValue(final StepTemplateConfig templateConfig, final int index, final String[] fields, Object object) throws NoSuchFieldException {
        if (object == null) {
            return "NULL";
        }
        for (int i = index; i < fields.length; ++i) {
            if (object.getClass().isArray()) {
                return parseArray(templateConfig, (Object[])object, i, fields);
            }
            if (object instanceof Iterable) {
                return parseCollection(templateConfig, (Iterable<?>)object, i, fields);
            }
            object = Accessible.on(object).field(fields[i]).getValue();
        }
        return parseDescendant(templateConfig, object);
    }
    
    private static String parseArray(final StepTemplateConfig templateConfig, final Object[] array, final int index, final String[] fields) throws NoSuchFieldException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(templateConfig.arrayStartSymbol());
        for (int i = 0; i < array.length; ++i) {
            stringBuilder.append(retrieveValue(templateConfig, index, fields, array[i]));
            if (i < array.length - 1) {
                stringBuilder.append(templateConfig.arrayElementDelimiter());
            }
        }
        stringBuilder.append(templateConfig.arrayEndSymbol());
        return stringBuilder.toString();
    }
    
    private static String parseCollection(final StepTemplateConfig templateConfig, final Iterable<?> iterable, final int index, final String[] fields) throws NoSuchFieldException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(templateConfig.iterableStartSymbol());
        final Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(retrieveValue(templateConfig, index, fields, iterator.next()));
            if (iterator.hasNext()) {
                stringBuilder.append(templateConfig.iterableElementDelimiter());
            }
        }
        stringBuilder.append(templateConfig.iterableEndSymbol());
        return stringBuilder.toString();
    }
    
    private static String parseDescendant(final StepTemplateConfig templateConfig, final Object descendant) {
        if (descendant == null) {
            return "NULL";
        }
        if (descendant.getClass().isArray()) {
            return parseDescendantArray(templateConfig, descendant);
        }
        if (descendant instanceof Iterable) {
            return parseDescendantCollection(templateConfig, (Iterable<?>)descendant);
        }
        return String.valueOf(descendant);
    }
    
    private static String parseDescendantArray(final StepTemplateConfig templateConfig, final Object array) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(templateConfig.arrayStartSymbol());
        for (int length = Array.getLength(array), i = 0; i < length; ++i) {
            stringBuilder.append(parseDescendant(templateConfig, Array.get(array, i)));
            if (i < length - 1) {
                stringBuilder.append(templateConfig.arrayElementDelimiter());
            }
        }
        stringBuilder.append(templateConfig.arrayEndSymbol());
        return stringBuilder.toString();
    }
    
    private static String parseDescendantCollection(final StepTemplateConfig templateConfig, final Iterable<?> iterable) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(templateConfig.iterableStartSymbol());
        final Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(parseDescendant(templateConfig, iterator.next()));
            if (iterator.hasNext()) {
                stringBuilder.append(templateConfig.iterableElementDelimiter());
            }
        }
        stringBuilder.append(templateConfig.iterableEndSymbol());
        return stringBuilder.toString();
    }
}
