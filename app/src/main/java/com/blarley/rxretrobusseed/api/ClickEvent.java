package com.blarley.rxretrobusseed.api;

import com.blarley.rxretrobusseed.annotationprocessor.processor.GenerateEvents;
import com.blarley.rxretrobusseed.annotationprocessor.processor.Publish;
import com.blarley.rxretrobusseed.models.ExampleGetModel;

import io.reactivex.Observable;

@GenerateEvents(retrofit = false)
public class ClickEvent {

    @Publish(tag = "setUpBomb", debounce = true, sticky = true)
    public Observable<ExampleGetModel> setUpBomb() {
        return Observable.just(new ExampleGetModel());
    }
}
