package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by esu on 7/10/17.
 */

public class UncachedEvent extends Publish {

    public UncachedEvent(String tag, boolean debounced, boolean sticky) {
        super(tag, false, debounced, sticky);
    }
}