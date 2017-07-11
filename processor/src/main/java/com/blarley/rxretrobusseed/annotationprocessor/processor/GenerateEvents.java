package com.blarley.rxretrobusseed.annotationprocessor.processor;

public @interface GenerateEvents {
    String baseUrl() default "";
    boolean retrofit() default true;
}
