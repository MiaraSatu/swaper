package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.FriendShipService;
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
public class FriendShipController {
    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private DBUserService userService;

    @GetMapping("/invitations/received")
    public Map<String, Object> getReceivedInvitation(@AuthenticationPrincipal Jwt jwt,  @Param("page") Integer page) {
        long limit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return friendShipService.getPaginedInvitation(false, subject, "/api/invitations/received", page, limit);
    }

    @GetMapping("/invitations/sent")
    public Map<String, Object> getSentInvitation(@AuthenticationPrincipal Jwt jwt, @Param("page") Integer page) {
        long limit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return friendShipService.getPaginedInvitation(true, subject, "/api/invitations/received", page, limit);
    }

    @PostMapping("/user/{userId}/invite")
    public String sendInvitation(@AuthenticationPrincipal Jwt jwt, @PathVariable int userId, @RequestBody String invitationMessage) {
        DBUser sender = userService.get(jwt.getClaim("sub")),
                receiver = userService.get(userId);
        friendShipService.send(sender, invitationMessage, receiver);
        return "Invitation sent successfully";
    }

    @GetMapping("/invitation/{friendShipId}/accept")
    public String acceptInvitation(@AuthenticationPrincipal Jwt jwt, @PathVariable int friendShipId) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        FriendShip friendShip = friendShipService.get(friendShipId);
        friendShipService.accept(subject, friendShip);
        return "Invitation accepted successfully";
    }

    @PostMapping("/invitation/{friendShipId}/refuse")
    public String refuseInvitation(@AuthenticationPrincipal Jwt jwt, @PathVariable int friendShipId, @RequestBody String refuseText) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        FriendShip friendShip = friendShipService.get(friendShipId);
        friendShipService.refuse(subject, friendShip, refuseText);
        return "Invitation refused successfully";
    }

    @GetMapping("/invitation/{friendShipId}/cancel")
    public ResponseEntity<String> cancelInvitation(@AuthenticationPrincipal Jwt jwt, @PathVariable int friendShipId) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        FriendShip friendShip = friendShipService.get(friendShipId);
        if(friendShip.getSender().getId() == subject.getId()) {
            friendShipService.cancel(friendShip);
            return new ResponseEntity<>("invitation cancel successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not allowed to cancel this invitation", HttpStatus.FORBIDDEN);
    }
}
