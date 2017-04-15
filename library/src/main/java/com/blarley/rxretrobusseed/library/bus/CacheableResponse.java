package com.blarley.rxretrobusseed.library.bus;

/**
 * Created by Blake on 4/15/17.
 */

public class CacheableResponse<T> {
    private final Class<T> type;
    private T response;

    public CacheableResponse(Class<T> type, T response) {
        this.type = type;
        this.response = response;
    }

}
