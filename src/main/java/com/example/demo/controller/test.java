package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class test {
    @GetMapping("/test1")
    public String Test(){
        return "hello";
    }
}
