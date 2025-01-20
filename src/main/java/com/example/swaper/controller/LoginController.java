package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private DBUserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/before_start")
    public String sayHello() {
        DBUser userOne = new DBUser();
        userOne.setName("user");
        userOne.setPassword(passwordEncoder.encode("user"));
        userOne.setEmail("user@example.com");
        userOne.setRole("USER");

        userService.add(userOne);
        return "Hello world!";
    }

    @GetMapping("/before_start/get_users")
    public List<DBUser> getUsers() {
        return userService.all();
    }

    @PostMapping("/login")
    public String getToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    @RequestMapping("/secret_page")
    public String testSecure() {
        return "Welcome to the secret page";
    }
}
