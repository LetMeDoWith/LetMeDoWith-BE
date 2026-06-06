package com.LetMeDoWith.LetMeDoWith.config;

import java.util.concurrent.Executor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

@TestConfiguration
// @EnableAsync // TODO - 비동기 테스트 필요시 활성화
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 테스트에서도 실제 비동기(별도 스레드)로 실행한다.
     * 동기 실행(Runnable::run)으로 두면 @Async 메서드의 @Transactional(REQUIRED)이 호출자 트랜잭션에 join되어,
     * 비동기 측 예외가 호출자 트랜잭션을 rollback-only로 마킹하는 부작용이 발생한다.
     * 별도 스레드에서 돌리면 트랜잭션 컨텍스트가 분리되어 운영과 동일한 격리 동작이 보장된다.
     */
    @Override
    public Executor getAsyncExecutor() {

        //        return Runnable::run; // 테스트 환경에서는 디버깅을 위해 비동기 작업 -> 동기화
        return new SimpleAsyncTaskExecutor("TestAsyncExecutor-");
    }
}
