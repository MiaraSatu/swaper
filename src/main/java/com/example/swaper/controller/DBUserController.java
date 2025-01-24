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
        return filePath.toString();
    }
}
