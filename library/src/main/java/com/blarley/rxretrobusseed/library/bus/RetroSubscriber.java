package com.blarley.rxretrobusseed.library.bus;

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
