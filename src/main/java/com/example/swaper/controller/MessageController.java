package com.example.swaper.controller;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/discussions")
    public Map<String, Object> getDiscussions(@AuthenticationPrincipal Jwt jwt, @Param("page") Integer page) {
        long listLimit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return messageService.getPaginedDiscussions(subject, "/api/discussions", page, listLimit);
    }

    @GetMapping("/discussion/{id}/{isBox}")
    public ResponseEntity<Object> getDiscussion(@AuthenticationPrincipal Jwt jwt, @PathVariable int id, @PathVariable boolean isBox) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        if(!isBox) {
            DBUser friend = userService.get(id);
            if(friend == null) return new ResponseEntity<>("Discussion #"+id+" not found", HttpStatus.NOT_FOUND);
            if(friendShipService.checkFriendShip(subject, friend)) return new ResponseEntity<>(friend, HttpStatus.OK);
        } else {
            // isBox case
            Box box = boxService.get(id);
            if(box == null) return new ResponseEntity<>("Discussion #"+id+" not found", HttpStatus.NOT_FOUND);
            if(memberShipService.checkMemberShip(box, subject)) return new ResponseEntity<>(box, HttpStatus.OK);
        }
        return new ResponseEntity<>("No discussion with #"+id, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/message/{receiverId}/{isBox}")
    public ResponseEntity<Object> sendMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody Message message, @PathVariable int receiverId, @PathVariable boolean isBox, @Param("reply_to") Integer replyTo) {
        DBUser sender = userService.get(jwt.getClaim("sub"));
        String messageType = isBox ? "inbox" : "sample";
        if(messageService.send(message, sender, receiverId, messageType, replyTo)) {
            if(!isBox) messagingTemplate.convertAndSend("/user/message/"+receiverId, message);
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
