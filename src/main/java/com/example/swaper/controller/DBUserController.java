package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class DBUserController {
    @Autowired
    private DBUserService dbUserService;

    @GetMapping("/discussers")
    public List<DBUser> getDiscussers(@AuthenticationPrincipal Jwt jwt) {
        DBUser subject = dbUserService.get(jwt.getClaim("sub"));
        return dbUserService.getMostFrequentedFriends(subject);
    }
}
