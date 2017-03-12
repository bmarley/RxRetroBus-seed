package com.blarley.rxretrobusseed.util;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Blake on 1/21/17.
 */
@Module
public class NetModule {

    OkHttpClient mOkHttpClient = new OkHttpClient()
            .newBuilder()
            .build();

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder() {

        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient);
    }
}
