package com.example.SpringBootCommunity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorityTestController {

    @GetMapping("/facebook")
    public String facebook(){
        return "facebook";
    }

    @GetMapping("/google")
    public String google(){
        return "gogle";
    }

    @GetMapping("/kakao")
    public String kakao(){
        return "kakao";
    }
}
