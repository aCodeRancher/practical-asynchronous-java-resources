package com.api.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    @Async("myCustomThreadPool")
    public CompletableFuture<String> getUser() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getSomeValue() == 10) {
            throw new RuntimeException("Something went wrong!");
        }

        return CompletableFuture.completedFuture("User name: John");
    }

    @Async
    public void storeUser() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getSomeValue() == 10) {
            throw new RuntimeException("Something went wrong!");
        }
    }

    private int getSomeValue() {
        return 10;
    }
}
