package com.blarley.rxretrobusseed.annotationprocessor.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Blake on 3/12/17.
 */

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Publish {
    String eventName();
    boolean cacheResult() default false;
    boolean debounce() default false;
}
