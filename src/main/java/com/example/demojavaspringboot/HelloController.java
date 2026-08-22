package com.example.demojavaspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    MojeServise mojeServise;


    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
    @GetMapping("/message")
    public String getMessage() {
        return mojeServise.getMojeMessage();
    }
}