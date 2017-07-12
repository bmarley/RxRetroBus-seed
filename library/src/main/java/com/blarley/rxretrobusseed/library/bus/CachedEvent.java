package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by esu on 7/10/17.
 */

public class CachedEvent extends Publish {

    public CachedEvent(String tag, boolean debounced) {
        super(tag, true, debounced, false, false);
    }
}