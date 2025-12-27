package com.LetMeDoWith.LetMeDoWith.support;

import com.LetMeDoWith.LetMeDoWith.LetMeDoWithBatchApplication;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        classes = LetMeDoWithBatchApplication.class,
        properties = {
            "spring.config.name=application,application-batch",
            "spring.main.web-application-type=none",
            "spring.batch.job.enabled=false",
        })
public @interface BatchIntegrationTest {}
