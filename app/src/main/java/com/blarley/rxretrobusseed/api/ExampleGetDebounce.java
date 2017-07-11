package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobusseed.annotationprocessor.processor.CachedEvent;
import com.blarley.rxretrobusseed.annotationprocessor.processor.GenerateEvents;
import com.blarley.rxretrobusseed.annotationprocessor.processor.UncachedEvent;
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
    @UncachedEvent(tag = "getUncachedRequestDebounce", debounce = true, sticky = true)
    Observable<ExampleGetModel> getUncachedRequest();

    @GET("{s}")
    @CachedEvent(tag = "getCachedRequestDebounce", debounce = true)
    Observable<ExampleGetModel> getCachedRequest(@Path("s") String s);
}
