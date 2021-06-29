// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.aspect;

import org.slf4j.LoggerFactory;
import io.reactivex.annotations.Nullable;
import com.epam.reportportal.utils.StepTemplateUtils;
import java.util.HashMap;
import com.epam.reportportal.annotations.StepTemplateConfig;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import com.epam.reportportal.annotations.Step;
import org.slf4j.Logger;

class StepNameUtils
{
    private static final Logger LOGGER;
    private static final String STEP_GROUP = "\\{([\\w$]+(\\.[\\w$]+)*)}";
    
    private StepNameUtils() {
    }
    
    static String getStepName(final Step step, final MethodSignature signature, final JoinPoint joinPoint) {
        final String nameTemplate = step.value();
        if (nameTemplate.trim().isEmpty()) {
            return signature.getMethod().getName();
        }
        final Matcher matcher = Pattern.compile("\\{([\\w$]+(\\.[\\w$]+)*)}").matcher(nameTemplate);
        final Map<String, Object> parametersMap = createParamsMapping(step.templateConfig(), signature, joinPoint.getArgs());
        final StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            final String templatePart = matcher.group(1);
            final String replacement = getReplacement(templatePart, parametersMap, step.templateConfig());
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement((replacement != null) ? replacement : matcher.group(0)));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
    
    static Map<String, Object> createParamsMapping(final StepTemplateConfig templateConfig, final MethodSignature signature, final Object... args) {
        final int paramsCount = Math.min(signature.getParameterNames().length, args.length);
        final Map<String, Object> paramsMapping = new HashMap<String, Object>();
        paramsMapping.put(templateConfig.methodNameTemplate(), signature.getMethod().getName());
        for (int i = 0; i < paramsCount; ++i) {
            paramsMapping.put(signature.getParameterNames()[i], args[i]);
            paramsMapping.put(Integer.toString(i), args[i]);
        }
        return paramsMapping;
    }
    
    @Nullable
    private static String getReplacement(final String templatePart, final Map<String, Object> parametersMap, final StepTemplateConfig templateConfig) {
        final String[] fields = templatePart.split("\\.");
        final String variableName = fields[0];
        if (!parametersMap.containsKey(variableName)) {
            StepNameUtils.LOGGER.error("Param - " + variableName + " was not found");
            return null;
        }
        final Object param = parametersMap.get(variableName);
        try {
            return StepTemplateUtils.retrieveValue(templateConfig, 1, fields, param);
        }
        catch (NoSuchFieldException e) {
            StepNameUtils.LOGGER.error("Unable to parse: " + templatePart);
            return null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)StepNameUtils.class);
    }
}
