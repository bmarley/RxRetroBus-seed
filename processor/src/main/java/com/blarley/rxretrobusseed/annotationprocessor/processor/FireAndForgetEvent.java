package com.blarley.rxretrobusseed.annotationprocessor.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface FireAndForgetEvent {
    String tag();
    boolean debounce() default false;
}
