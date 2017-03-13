package com.blarley.rxretrobusseed;

import android.app.Application;

import com.blarley.rxretrobusseed.api.Clients;
import com.blarley.rxretrobusseed.bus.RxBus;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class App extends Application {
    private OkHttpClient okHttpClient;
    private Retrofit.Builder retrofitBuilder;

    public static Clients clients;
    public static RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient = new OkHttpClient()
                .newBuilder()
                .build();

        retrofitBuilder = new Retrofit.Builder()
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient);

        clients = new Clients(retrofitBuilder);
        bus = new RxBus();
    }
}
