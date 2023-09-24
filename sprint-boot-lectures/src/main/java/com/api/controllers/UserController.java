package com.api.controllers;

import com.api.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String getUser() throws InterruptedException {
       AtomicReference<String> finalResult = new AtomicReference<>("");

        CompletableFuture<String> future = userService.getUser().whenCompleteAsync((result, ex) -> {
           finalResult.set(result);
        });

        while (finalResult.get().isEmpty()) {
           System.out.println("Current thread: " + Thread.currentThread().getName());
            Thread.sleep(1000);
       }

       return finalResult.get();
    }
}
