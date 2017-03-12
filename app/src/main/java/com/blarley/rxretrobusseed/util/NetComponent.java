package com.blarley.rxretrobusseed.util;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Blake on 1/21/17.
 */
@Singleton
@Component(modules= { NetModule.class })
public interface NetComponent {
    void inject(ClientsModule clientsModule);
}
