package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    public Executor getAsyncExecutor() {
        // TODO - 서버 Spec 및 초기 트래픽 모니터링을 통해 수치 조정
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.setTaskDecorator(new ContextPropagatingDecorator());
        executor.initialize();
        return executor;
    }


    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("message: {}, method:{}.{}(), parameter: {}",
                    ex.getMessage(),
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    Arrays.toString(params),
                    ex);
        };
    }

    /**
     * 비동기 Thread에 Context 전파
     */
    public static class ContextPropagatingDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            ZoneId timeZone = TimeZoneContextHolder.getTimeZone();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    TimeZoneContextHolder.setTimeZone(timeZone);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                    TimeZoneContextHolder.clearTimeZoneHolder();
                }
            };
        }
    }
}
