package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by Blake on 4/14/17.
 */

public abstract class RetroSubscriber<T> {
    private String tag;

    public RetroSubscriber(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public abstract void onLoading();
    public abstract void onSuccess(T response);
    public abstract void onError(Throwable throwable);
}
