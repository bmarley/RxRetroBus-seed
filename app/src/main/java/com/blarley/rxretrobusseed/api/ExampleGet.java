package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobus.annotations.CachedEvent;
import com.blarley.rxretrobus.annotations.UncachedEvent;
import com.blarley.rxretrobusseed.models.ExampleGetModel;
import com.blarley.rxretrobus.annotations.GenerateEvents;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Blake on 1/21/17.
 */

@GenerateEvents(baseUrl = "http://api.blarley.com/")
public interface ExampleGet {
    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequest", sticky = true)
    Observable<ExampleGetModel> getUncachedRequest();

    @GET("example-get")
    @CachedEvent(tag = "getCachedRequest")
    Observable<ExampleGetModel> getCachedRequest();
}
