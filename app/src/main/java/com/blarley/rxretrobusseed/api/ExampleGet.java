package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobusseed.models.ExampleGetModel;
import com.blarley.rxretrobusseed.annotationprocessor.processor.GenerateEvents;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Blake on 1/21/17.
 */

@GenerateEvents
public interface ExampleGet {
    @GET("example-get")
    Observable<ExampleGetModel> exampleGet();
}
