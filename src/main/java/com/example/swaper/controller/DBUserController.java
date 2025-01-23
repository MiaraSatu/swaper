package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class DBUserController {
    @Autowired
    private DBUserService userService;

    @GetMapping("/discussers")
    public List<DBUser> getBestFriends(@AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.getMostFrequentedFriends(subject);
    }

    @GetMapping("/discussers/search")
    public List<DBUser> searchDiscusser(@Param("kw") String kw, @AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.searchDiscusser(kw, subject);
    }

    @GetMapping("/{id}")
    public DBUser getUser(@PathVariable int id) {
        DBUser user = userService.get(id);
        return user;
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Object> getFriends(@PathVariable int userId, @Param("page") Integer page) {
        long limit = 10L;
        DBUser subject = userService.get(userId);
        if(null != subject) {
            Map<String, Object> result = userService.getPaginedFriends(subject, "/api/"+userId+"/friends", page, limit);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}
