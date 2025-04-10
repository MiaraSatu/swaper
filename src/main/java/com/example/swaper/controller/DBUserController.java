package com.example.swaper.controller;

import com.example.swaper.model.DBUser;
import com.example.swaper.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class DBUserController {
    @Autowired
    private DBUserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable int userId) {
        DBUser user = userService.get(userId),
                subject = userService.get(jwt.getClaim("sub"));
        userService.complete(user, subject);
        if(user == null) return new ResponseEntity<>("User #"+userId+" not found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/discussers")
    public List<DBUser> getBestFriends(@AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.getMostFrequentedFriends(subject);
    }

    @GetMapping("/suggestions")
    public Map<String, Object> getNotFriends(@AuthenticationPrincipal Jwt jwt, @Param("page") Integer page) {
        long limit = 10L;
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.getPaginedSuggestions(subject, "/api/users/suggestions", page, limit);
    }

    @GetMapping("/discussers/search")
    public List<DBUser> searchDiscusser(@Param("kw") String kw, @AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.searchDiscusser(kw, subject);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUser(@AuthenticationPrincipal Jwt jwt, @Param("kw") String kw) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return new ResponseEntity<>(userService.search(kw, subject), HttpStatus.OK);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Object> getFriends(@PathVariable int userId, @Param("page") Integer page) {
        long limit = 10L;
        DBUser subject = userService.get(userId);
        if(null != subject) {
            Map<String, Object> result = userService.getPaginedFriends(subject, "/api/users/"+userId+"/friends", page, limit);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/")
    public ResponseEntity<Object> editUser(@AuthenticationPrincipal Jwt jwt, @RequestBody DBUser edited) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        if(edited.getId() != subject.getId()) return new ResponseEntity<>("Not allowed", HttpStatus.FORBIDDEN);
        if(!userService.update(edited)) return new ResponseEntity<>("Invalid user data", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @PostMapping("/picture")
    public ResponseEntity<Object> setProfilPicture(@AuthenticationPrincipal Jwt jwt, @RequestParam("file")MultipartFile file) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        try {
            String imageUrl = saveImage(file);
            subject.setImageUrl(imageUrl);
            userService.add(subject);
            return new ResponseEntity<>(subject, HttpStatus.OK);
        } catch(IOException e) {
            return new ResponseEntity<>("Image upload error"+ e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get("./uploads/user");
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/user/"+fileName;
    }
}
