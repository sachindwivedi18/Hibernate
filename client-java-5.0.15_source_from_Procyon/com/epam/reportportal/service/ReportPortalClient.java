// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service;

import com.epam.reportportal.restendpoint.http.annotation.Close;
import com.epam.ta.reportportal.ws.model.BatchSaveOperatingRS;
import com.epam.reportportal.restendpoint.http.annotation.Multipart;
import com.epam.reportportal.restendpoint.http.MultiPartRequest;
import com.epam.ta.reportportal.ws.model.EntryCreatedAsyncRS;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.item.ItemCreatedRS;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.reportportal.restendpoint.http.annotation.Path;
import com.epam.ta.reportportal.ws.model.launch.LaunchResource;
import com.epam.ta.reportportal.ws.model.launch.MergeLaunchesRQ;
import com.epam.reportportal.restendpoint.http.HttpMethod;
import com.epam.reportportal.restendpoint.http.annotation.Request;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRS;
import io.reactivex.Maybe;
import com.epam.reportportal.restendpoint.http.annotation.Body;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;

public interface ReportPortalClient
{
    @Request(method = HttpMethod.POST, url = "/launch")
    Maybe<StartLaunchRS> startLaunch(@Body final StartLaunchRQ p0);
    
    @Request(method = HttpMethod.POST, url = "/launch/merge")
    Maybe<LaunchResource> mergeLaunches(@Body final MergeLaunchesRQ p0);
    
    @Request(method = HttpMethod.PUT, url = "/launch/{launchId}/finish")
    Maybe<OperationCompletionRS> finishLaunch(@Path("launchId") final String p0, @Body final FinishExecutionRQ p1);
    
    @Request(method = HttpMethod.POST, url = "/item/")
    Maybe<ItemCreatedRS> startTestItem(@Body final StartTestItemRQ p0);
    
    @Request(method = HttpMethod.POST, url = "/item/{parent}")
    Maybe<ItemCreatedRS> startTestItem(@Path("parent") final String p0, @Body final StartTestItemRQ p1);
    
    @Request(method = HttpMethod.PUT, url = "/item/{itemId}")
    Maybe<OperationCompletionRS> finishTestItem(@Path("itemId") final String p0, @Body final FinishTestItemRQ p1);
    
    @Request(method = HttpMethod.POST, url = "/log/")
    Maybe<EntryCreatedAsyncRS> log(@Body final SaveLogRQ p0);
    
    @Request(method = HttpMethod.POST, url = "/log/")
    Maybe<BatchSaveOperatingRS> log(@Body @Multipart final MultiPartRequest p0);
    
    @Request(method = HttpMethod.GET, url = "/launch/uuid/{launchUuid}")
    Maybe<LaunchResource> getLaunchByUuid(@Path("launchUuid") final String p0);
    
    @Close
    void close();
}
