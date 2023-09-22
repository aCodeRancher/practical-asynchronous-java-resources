package com.api.controllers;

import com.api.services.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class HelloController {

    private final HelloService service;

    public HelloController(HelloService service){
         this.service = service;
    }

    @GetMapping("/hello")
    public CompletableFuture<String> helloBack(){
        return service.processRequest();
    }
}
