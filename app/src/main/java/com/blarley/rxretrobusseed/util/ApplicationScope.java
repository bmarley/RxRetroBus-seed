package com.blarley.rxretrobusseed.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Blake on 1/21/17.
 */

@Scope
@Documented
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ApplicationScope {
}
