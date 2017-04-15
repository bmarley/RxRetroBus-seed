package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by Blake on 4/14/17.
 */

public abstract class RetroSubscriber<T> {
    private String tagName;

    public RetroSubscriber(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return this.tagName;
    }

    public abstract void onLoading();
    public abstract void onSuccess(T response);
    public abstract void onError(Throwable throwable);
}
