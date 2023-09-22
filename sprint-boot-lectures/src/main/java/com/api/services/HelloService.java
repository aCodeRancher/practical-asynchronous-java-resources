package com.api.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class HelloService {

    @Async
    public CompletableFuture<String> processRequest(){
        sleep(5000L);
        return CompletableFuture.completedFuture("Request executed!");
    }

    private void sleep(Long time){
        try{
            Thread.sleep(time);
        }
        catch(InterruptedException e){
             throw new RuntimeException(e);
        }
    }
}
