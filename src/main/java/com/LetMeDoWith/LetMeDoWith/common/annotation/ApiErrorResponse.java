package com.LetMeDoWith.LetMeDoWith.common.annotation;

import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(ApiErrorResponses.class)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponse {

    FailResponseStatus status();

    String description() default "";
}
