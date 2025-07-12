package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.ZoneId;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
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
