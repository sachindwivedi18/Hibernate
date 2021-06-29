// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.tree;

import org.slf4j.LoggerFactory;
import com.epam.reportportal.message.TypeAwareByteSource;
import com.epam.reportportal.utils.files.Utils;
import com.epam.reportportal.restendpoint.http.MultiPartRequest;
import java.util.List;
import com.epam.reportportal.utils.http.HttpRequestUtils;
import rp.com.google.common.collect.Lists;
import java.io.IOException;
import com.epam.ta.reportportal.ws.model.BatchSaveOperatingRS;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.epam.ta.reportportal.ws.model.EntryCreatedAsyncRS;
import java.io.File;
import java.util.Date;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import io.reactivex.Maybe;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.reportportal.service.ReportPortalClient;
import org.slf4j.Logger;

public class ItemTreeReporter
{
    private static final Logger LOGGER;
    
    private ItemTreeReporter() {
    }
    
    public static Maybe<String> startItem(final ReportPortalClient reportPortalClient, final StartTestItemRQ startTestItemRQ, final Maybe<String> launchUuid, final TestItemTree.TestItemLeaf testItemLeaf) {
        final Maybe<String> parent = testItemLeaf.getParentId();
        if (parent != null && launchUuid != null) {
            return sendStartItemRequest(reportPortalClient, launchUuid, parent, startTestItemRQ);
        }
        return (Maybe<String>)Maybe.empty();
    }
    
    public static Maybe<OperationCompletionRS> finishItem(final ReportPortalClient reportPortalClient, final FinishTestItemRQ finishTestItemRQ, final Maybe<String> launchUuid, final TestItemTree.TestItemLeaf testItemLeaf) {
        final Maybe<String> item = testItemLeaf.getItemId();
        final Maybe<OperationCompletionRS> finishResponse = testItemLeaf.getFinishResponse();
        if (item == null || launchUuid == null) {
            return (Maybe<OperationCompletionRS>)Maybe.empty();
        }
        if (finishResponse != null) {
            final Throwable t = finishResponse.ignoreElement().blockingGet();
            if (t != null) {
                ItemTreeReporter.LOGGER.warn("A main item finished with error", t);
            }
        }
        return sendFinishItemRequest(reportPortalClient, launchUuid, item, finishTestItemRQ);
    }
    
    public static boolean sendLog(final ReportPortalClient reportPortalClient, final String level, final String message, final Date logTime, final Maybe<String> launchUuid, final TestItemTree.TestItemLeaf testItemLeaf) {
        final Maybe<String> itemId = testItemLeaf.getItemId();
        if (launchUuid != null && itemId != null) {
            sendLogRequest(reportPortalClient, launchUuid, itemId, level, message, logTime).subscribe();
            return true;
        }
        return false;
    }
    
    public static boolean sendLog(final ReportPortalClient reportPortalClient, final String level, final String message, final Date logTime, final File file, final Maybe<String> launchUuid, final TestItemTree.TestItemLeaf testItemLeaf) {
        final Maybe<String> itemId = testItemLeaf.getItemId();
        if (launchUuid != null && itemId != null) {
            sendLogMultiPartRequest(reportPortalClient, launchUuid, itemId, level, message, logTime, file).subscribe();
            return true;
        }
        return false;
    }
    
    private static Maybe<String> sendStartItemRequest(final ReportPortalClient reportPortalClient, final Maybe<String> launchUuid, final Maybe<String> parent, final StartTestItemRQ startTestItemRQ) {
        startTestItemRQ.setLaunchUuid((String)launchUuid.blockingGet());
        return (Maybe<String>)reportPortalClient.startTestItem((String)parent.blockingGet(), startTestItemRQ).map(EntryCreatedAsyncRS::getId).cache();
    }
    
    private static Maybe<OperationCompletionRS> sendFinishItemRequest(final ReportPortalClient reportPortalClient, final Maybe<String> launchUuid, final Maybe<String> item, final FinishTestItemRQ finishTestItemRQ) {
        finishTestItemRQ.setLaunchUuid((String)launchUuid.blockingGet());
        return reportPortalClient.finishTestItem((String)item.blockingGet(), finishTestItemRQ);
    }
    
    private static Maybe<EntryCreatedAsyncRS> sendLogRequest(final ReportPortalClient reportPortalClient, final Maybe<String> launchUuid, final Maybe<String> itemUuid, final String level, final String message, final Date logTime) {
        final SaveLogRQ saveLogRequest = createSaveLogRequest((String)launchUuid.blockingGet(), (String)itemUuid.blockingGet(), level, message, logTime);
        return reportPortalClient.log(saveLogRequest);
    }
    
    private static Maybe<BatchSaveOperatingRS> sendLogMultiPartRequest(final ReportPortalClient reportPortalClient, final Maybe<String> launchUuid, final Maybe<String> itemId, final String level, final String message, final Date logTime, final File file) {
        final SaveLogRQ saveLogRequest = createSaveLogRequest((String)launchUuid.blockingGet(), (String)itemId.blockingGet(), level, message, logTime);
        try {
            saveLogRequest.setFile(createFileModel(file));
        }
        catch (IOException e) {
            return (Maybe<BatchSaveOperatingRS>)Maybe.error((Throwable)e);
        }
        final MultiPartRequest multiPartRequest = HttpRequestUtils.buildLogMultiPartRequest(Lists.newArrayList(saveLogRequest));
        return reportPortalClient.log(multiPartRequest);
    }
    
    private static SaveLogRQ createSaveLogRequest(final String launchUuid, final String itemId, final String level, final String message, final Date logTime) {
        final SaveLogRQ saveLogRQ = new SaveLogRQ();
        saveLogRQ.setLaunchUuid(launchUuid);
        saveLogRQ.setItemUuid(itemId);
        saveLogRQ.setLevel(level);
        saveLogRQ.setLogTime(logTime);
        saveLogRQ.setMessage(message);
        return saveLogRQ;
    }
    
    private static SaveLogRQ.File createFileModel(final File file) throws IOException {
        final TypeAwareByteSource data = Utils.getFile(file);
        final SaveLogRQ.File fileModel = new SaveLogRQ.File();
        fileModel.setContent(data.read());
        fileModel.setContentType(data.getMediaType());
        fileModel.setName(file.getName());
        return fileModel;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemTreeReporter.class);
    }
}
