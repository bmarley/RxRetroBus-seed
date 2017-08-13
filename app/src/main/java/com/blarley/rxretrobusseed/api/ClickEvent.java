package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobus.annotations.GenerateEvents;
import com.blarley.rxretrobus.annotations.UncachedEvent;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

@GenerateEvents(retrofit = false)
public class ClickEvent {

    @UncachedEvent(tag = "setUpBomb", debounce = true, sticky = true)
    public Observable<ExampleGetModel> setUpBomb() {
        return Observable.just(new ExampleGetModel()).delay(5, TimeUnit.SECONDS);
    }
}
