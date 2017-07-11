package com.blarley.rxretrobusseed.annotationprocessor.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Publish {
    String tag();
    boolean cache() default false;
    boolean debounce() default false;
    boolean sticky() default false;
}
