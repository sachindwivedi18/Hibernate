// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.aspect;

import com.epam.reportportal.utils.AttributeParser;
import com.epam.reportportal.annotations.attribute.Attributes;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import java.util.Date;
import com.epam.reportportal.listeners.ItemStatus;
import java.util.Calendar;
import java.util.Set;
import com.epam.reportportal.annotations.UniqueID;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import org.aspectj.lang.JoinPoint;
import com.epam.reportportal.annotations.Step;
import org.aspectj.lang.reflect.MethodSignature;

class StepRequestUtils
{
    private StepRequestUtils() {
    }
    
    static StartTestItemRQ buildStartStepRequest(final MethodSignature signature, final Step step, final JoinPoint joinPoint) {
        final UniqueID uniqueIdAnnotation = signature.getMethod().getAnnotation(UniqueID.class);
        final String uniqueId = (uniqueIdAnnotation != null) ? uniqueIdAnnotation.value() : null;
        final String name = StepNameUtils.getStepName(step, signature, joinPoint);
        final StartTestItemRQ request = new StartTestItemRQ();
        if (uniqueId != null && !uniqueId.trim().isEmpty()) {
            request.setUniqueId(uniqueId);
        }
        request.setAttributes((Set)createStepAttributes(signature));
        if (!step.description().isEmpty()) {
            request.setDescription(step.description());
        }
        request.setName(name);
        request.setStartTime(Calendar.getInstance().getTime());
        request.setType("STEP");
        request.setHasStats(false);
        return request;
    }
    
    static FinishTestItemRQ buildFinishStepRequest(final ItemStatus status, final Date endTime) {
        final FinishTestItemRQ rq = new FinishTestItemRQ();
        rq.setEndTime(endTime);
        rq.setStatus(status.name());
        return rq;
    }
    
    private static Set<ItemAttributesRQ> createStepAttributes(final MethodSignature methodSignature) {
        final Attributes attributesAnnotation = methodSignature.getMethod().getAnnotation(Attributes.class);
        if (attributesAnnotation != null) {
            return AttributeParser.retrieveAttributes(attributesAnnotation);
        }
        return null;
    }
}
