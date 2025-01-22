package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DBUserService userService;

    @GetMapping("/discussions")
    public Map<String, Object> getDiscussions(@AuthenticationPrincipal Jwt jwt, @Param("page") Integer page) {
        long listLimit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return messageService.getPaginedDiscussions(subject, "/api/discussions", page, listLimit);
    }
}
