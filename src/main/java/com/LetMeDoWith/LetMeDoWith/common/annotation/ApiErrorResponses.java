package com.LetMeDoWith.LetMeDoWith.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Swagger API Docs에서 각 Endpoint 별 응답 가능한 Error 목록을 표시하기 위한 Annotation. 각 Endpoint에 본 애너테이션을 사용하여
 * FailResponseStatus를 지정하는 것 만으로도 Swagger Docs에 에러 Example이 생성된다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponses {

    ApiErrorResponse[] value() default {};
}
