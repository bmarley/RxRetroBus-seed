package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobus.annotations.CachedEvent;
import com.blarley.rxretrobus.annotations.GenerateEvents;
import com.blarley.rxretrobus.annotations.UncachedEvent;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Blake on 1/21/17.
 */

@GenerateEvents(baseUrl = "http://api.blarley.com/")
public interface ExampleGetDebounce {
    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestDebounceSticky", debounce = true, sticky = true)
    Observable<ExampleGetModel> getUncachedRequestSticky();

    @GET("example-get")
    @UncachedEvent(tag = "getUncachedRequestDebounceNonSticky", debounce = true)
    Observable<ExampleGetModel> getUncachedRequestNonSticky();

    @GET("{s}")
    @CachedEvent(tag = "getCachedRequestDebounce", debounce = true)
    Observable<ExampleGetModel> getCachedRequest(@Path("s") String s);
}
