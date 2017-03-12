package com.blarley.rxretrobusseed.api;

import android.app.Application;

import com.blarley.rxretrobusseed.App;

import javax.inject.Inject;

/**
 * Created by Blake on 3/11/17.
 */

public class Clients {
    @Inject
    public ExampleGet exampleGet;

    public Clients(Application application) {
        ((App) application).getClientsComponent().inject(this);
    }
}
