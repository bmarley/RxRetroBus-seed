package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by esu on 7/10/17.
 */

abstract public class Publish {
    private String tag;
    private boolean cached;
    private boolean debounced;
    private boolean sticky;
    private boolean fireAndForget;


    public Publish(String tag, boolean cached, boolean debounced, boolean sticky, boolean fireAndForget) {
        this.tag = tag;
        this.cached = cached;
        this.debounced = debounced;
        this.sticky = sticky;
        this.fireAndForget = fireAndForget;
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


    public boolean isSticky() {
        return sticky;
    }


    public boolean isFireAndForget() {
        return fireAndForget;
    }
}