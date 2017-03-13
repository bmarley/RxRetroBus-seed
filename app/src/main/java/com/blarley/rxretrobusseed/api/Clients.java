package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobusseed.annotationprocessor.generated.RxRetroBusExampleGet;

import retrofit2.Retrofit;

/**
 * Created by Blake on 3/11/17.
 */

public class Clients {
    public RxRetroBusExampleGet ExampleGet;

    public Clients(Retrofit.Builder retrofitBuilder) {
        this.ExampleGet = new RxRetroBusExampleGet(retrofitBuilder);
    }
}
