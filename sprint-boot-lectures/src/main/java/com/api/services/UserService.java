package com.api.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

   @Async
    public  CompletableFuture<String>  getUser() {

        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("User name: John");
    }

}
