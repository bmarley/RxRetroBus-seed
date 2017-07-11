package com.blarley.rxretrobusseed.library.bus;

public class Request<T> {
    private T success;
    private Throwable error;
    private Boolean loading;
    private Boolean sticky;

    public Request(T success, Throwable error, Boolean loading, Boolean sticky) {
        this.success = success;
        this.loading = loading;
        this.error = error;
        this.sticky = sticky;
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


    public Boolean isSticky() {
        return sticky;
    }


    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }


    public Boolean isError() {
        return this.error != null;
    }

    public Boolean isLoading() {
        return loading;
    }
}
