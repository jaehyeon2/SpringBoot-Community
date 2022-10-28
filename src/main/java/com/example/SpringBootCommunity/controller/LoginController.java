package com.example.SpringBootCommunity.controller;

import com.example.SpringBootCommunity.annotation.SocialUser;
import com.example.SpringBootCommunity.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping(value={"/facebook/complete", "/google/complete", "/kakao/complete"})
    public String loginComplete(@SocialUser User user){
        return "redirect:/board/list";
    }
}
