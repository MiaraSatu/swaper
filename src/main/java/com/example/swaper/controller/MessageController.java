package com.example.swaper.controller;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.MemberShip;
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

import java.util.ArrayList;
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

    @PostMapping("/discussion/box")
    public Box createBox(@AuthenticationPrincipal Jwt jwt, @RequestBody Box box) {
        DBUser admin = userService.get(jwt.getClaim("sub"));
        boxService.add(box, admin);
        return box;
    }

    @PostMapping("/discussion/box/{boxId}/add_members")
    public ResponseEntity<String> addUsersToBox(@AuthenticationPrincipal Jwt jwt, @RequestBody ArrayList<Integer> membersId, @PathVariable int boxId) {
        DBUser admin = userService.get(jwt.getClaim("sub"));
        Box box = boxService.get(boxId);
        boolean authorized = boxService.checkMemberIsAdmin(box, admin);
        if(authorized) {
            List<DBUser> members = membersId.stream().map(id -> userService.get(id)).toList();
            boxService.addUsers(box, members);
            return new ResponseEntity<>("New members added successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not authorized to add new member", HttpStatus.FORBIDDEN);
    }
}
