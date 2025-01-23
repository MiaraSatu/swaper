package com.example.swaper.controller;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.service.BoxService;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.MemberShipService;
import com.example.swaper.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/message/{receiverId}/{isBox}")
    public ResponseEntity<Object> sendMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody Message message, @PathVariable int receiverId, @PathVariable boolean isBox, @Param("reply_to") Integer replyTo) {
        DBUser sender = userService.get(jwt.getClaim("sub"));
        String messageType = isBox ? "inbox" : "sample";
        if(messageService.send(message, sender, receiverId, messageType, replyTo)) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>("Message not sent", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/messages/{receiverId}/{isBox}")
    public ResponseEntity<Object> getMessages(@AuthenticationPrincipal Jwt jwt, @PathVariable int receiverId, @PathVariable boolean isBox, @Param("page") Integer page) {
        long limit = 10;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        String type = isBox ? "inBox" : "sample";
        Map<String, Object> result = messageService.getPaginedMessagesExchanged(subject, type, receiverId, "/api/messages/"+receiverId+"/"+(isBox?1:0), page, limit);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
