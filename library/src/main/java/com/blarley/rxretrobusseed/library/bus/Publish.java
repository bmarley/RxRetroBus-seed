package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by esu on 7/10/17.
 */

public class Publish {
    String tag;
    boolean cached;
    boolean debounced;

    public Publish(String tag, boolean cached, boolean debounced) {
        this.tag = tag;
        this.cached = cached;
        this.debounced = debounced;
    }


    public String getTag() {
        return tag;
    }


    public boolean isCached() {
        return cached;
    }


    public boolean isDebounced() {
        return debounced;
    }
}
