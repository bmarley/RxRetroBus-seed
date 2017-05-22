package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by Blake on 4/15/17.
 */

public class CacheableRequest<T> {
    private T success;
    private Throwable error;
    private Boolean loading;

    public CacheableRequest(T success, Throwable error, Boolean loading) {
        this.success = success;
        this.loading = loading;
        this.error = error;
    }

    public T getSuccess() {
        return success;
    }

    public void setSuccess(T success) {
        this.success = success;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Boolean isError() {
        return this.error != null;
    }

    public Boolean isLoading() {
        return loading;
    }
}
