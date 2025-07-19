package com.LetMeDoWith.LetMeDoWith.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

@TestConfiguration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return Runnable::run; // 현재 쓰레드에서 실행 (비동기 X)
    }

}
