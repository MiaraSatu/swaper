package com.example.swaper.controller;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.MemberShip;
import com.example.swaper.service.BoxService;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.MemberShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BoxController {

    @Autowired
    private DBUserService userService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private MemberShipService memberShipService;

    @PostMapping("/box")
    public Box createBox(@AuthenticationPrincipal Jwt jwt, @RequestBody Box box) {
        DBUser admin = userService.get(jwt.getClaim("sub"));
        boxService.add(box, admin);
        return box;
    }

    @PostMapping("/box/{boxId}/users")
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

    @DeleteMapping("/box/{boxId}/user/{memberId}")
    public ResponseEntity<String> removeUserFromBox(@AuthenticationPrincipal Jwt jwt, @PathVariable int boxId, @PathVariable int memberId) {
        Box box = boxService.get(boxId);
        DBUser admin = userService.get(jwt.getClaim("sub")),
            member = userService.get(memberId);
        if(boxService.checkMemberIsAdmin(box, admin)) {
            boxService.removeUser(box, member);
            return new ResponseEntity<>("Member removed successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not allowed to remove the member from the box", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/box/{boxId}/users")
    public ResponseEntity<Object> getMembers(@AuthenticationPrincipal Jwt jwt, @PathVariable int boxId) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        Box box = boxService.get(boxId);
        if(box == null) return new ResponseEntity<>("Box not found", HttpStatus.NOT_FOUND);
        MemberShip memberShip = memberShipService.get(box, subject);
        if(memberShip == null) return new ResponseEntity<>("Not allowed", HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(userService.getBoxMembers(box), HttpStatus.OK);
    }
}
