package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DBUserService userService;

    @GetMapping("/discussions")
    public List<Message> getDiscussions(@AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return messageService.getDiscussions(subject);
    }
}
