package com.api.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;
import java.util.concurrent.*;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {

        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setName("custom-thread-1");
            return t;
        };

        return new ThreadPoolExecutor(
            4,
            5,
            10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                threadFactory
        );
    }

    @Bean("myCustomThreadPool")
    public Executor myCustomThreadPool() {
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setName("custom-thread-2");
            return t;
        };

        return new ThreadPoolExecutor(
                4,
                5,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                threadFactory
        );
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {

        };
    }
}
