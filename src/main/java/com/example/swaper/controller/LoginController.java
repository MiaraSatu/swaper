package com.example.swaper.controller;

import com.example.swaper.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/before_start")
    public String sayHello() {
        return "Hello world!";
    }

    @RequestMapping("/login")
    public String getToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    @RequestMapping("/secret_page")
    public String testSecure() {
        return "Welcome to the secret page";
    }
}
