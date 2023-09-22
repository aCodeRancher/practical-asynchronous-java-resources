package com.api.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.*;

@Component
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {


    private final MeterRegistry meterRegistry;

    public AsyncConfig(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Executor getAsyncExecutor() {

        ExecutorService threadPool = new  ThreadPoolExecutor(
            4,
            10,
            100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
               getThreadFactory(),
                (r,executor)-> System.out.println("Ooops! A task got rejected.")
        );
       return ExecutorServiceMetrics.monitor(meterRegistry, threadPool,"custom-thread-pool"
               , new ArrayList<>());
    }

    private ThreadFactory getThreadFactory() {
        return r -> {
            Thread t = new Thread(r);
            t.setName("custom-thread-1");
            return t;
            };
    }
   /* @Bean("myCustomThreadPool")
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
    }*/
}
