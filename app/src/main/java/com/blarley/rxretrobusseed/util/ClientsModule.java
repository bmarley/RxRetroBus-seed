package com.blarley.rxretrobusseed.util;

import com.blarley.rxretrobusseed.api.ExampleGet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Blake on 1/21/17.
 */
@Module
public class ClientsModule {
    @Inject Retrofit.Builder retrofitBuilder;

    public ClientsModule (NetComponent netComponent){
        netComponent.inject(this);
    }

    @Provides
    @ApplicationScope
    public ExampleGet providesExampleGetInterface() {
        return create(ExampleGet.class, "http://api.blarley.com/", retrofitBuilder);
    }

    private <T> T create(final Class<T> clazz, String baseEndpoint, Retrofit.Builder retrofitBuilder) {
        final T client = retrofitBuilder.baseUrl(baseEndpoint).build().create(clazz);

        return clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("Current hack to hijack the observable and add it to the bus - ideally this would be a separate" +
                                "call adapter for retrofit");
                        return clazz.getMethod(method.getName()).invoke(client, args);

                    }
                })
        );
    }

}
