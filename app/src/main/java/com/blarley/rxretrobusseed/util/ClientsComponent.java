package com.blarley.rxretrobusseed.util;

import com.blarley.rxretrobusseed.api.Clients;

import dagger.Component;

/**
 * Created by Blake on 1/21/17.
 */
@ApplicationScope
@Component(modules = ClientsModule.class)
public interface ClientsComponent {
    void inject(Clients clients);
}