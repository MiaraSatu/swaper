package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.service.BoxService;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.MemberShipService;
import com.example.swaper.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DBUserService userService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private MemberShipService memberShipService;

    @GetMapping("/discussions")
    public Map<String, Object> getDiscussions(@AuthenticationPrincipal Jwt jwt, @Param("page") Integer page) {
        long listLimit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return messageService.getPaginedDiscussions(subject, "/api/discussions", page, listLimit);
    }


}
