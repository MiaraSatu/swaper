package com.example.swaper.controller;

import com.example.swaper.model.*;
import com.example.swaper.repository.*;
import com.example.swaper.service.DBUserService;
import com.example.swaper.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Member;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private DBUserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FriendShipRepository friendShipRepository;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private MemberShipRepository memberShipRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/before_start")
    public String sayHello() {
        // user initialization
        DBUser userOne = new DBUser();
        userOne.setName("user");
        userOne.setPassword(passwordEncoder.encode("user"));
        userOne.setEmail("user@example.com");
        userOne.setRole("USER");
        userService.add(userOne);

        DBUser userTwo = new DBUser();
        userTwo.setName("johndoe");
        userTwo.setPassword(passwordEncoder.encode("johndoe"));
        userTwo.setEmail("johndoe@example.com");
        userTwo.setRole("USER");
        userService.add(userTwo);

        DBUser userThree = new DBUser();
        userThree.setName("janedoe");
        userThree.setPassword(passwordEncoder.encode("janedoe"));
        userThree.setEmail("janedoe@example.com");
        userThree.setRole("USER");
        userService.add(userThree);

        // friendShip initialization
        FriendShip userOneTwo = new FriendShip();
        userOneTwo.setSender(userOne);
        userOneTwo.setReceiver(userTwo);
        userOneTwo.setAccepted(true);
        userOneTwo.setInvitationSeen(true);
        friendShipRepository.save(userOneTwo);

        FriendShip userTwoThree = new FriendShip();
        userTwoThree.setSender(userThree);
        userTwoThree.setReceiver(userTwo);
        userTwoThree.setAccepted(true);
        userTwoThree.setInvitationSeen(true);
        friendShipRepository.save(userTwoThree);

        // message initialization
        Message message1 = new Message();
        message1.setContent("First message");
        message1.setType("sample");
        message1.setSender(userTwo);
        message1.setReceiver(userThree);
        Instant now = Instant.now();
        Date cat1 = Date.from(now);
        message1.setCreatedAt(cat1);
        messageRepository.save(message1);

        Message message2 = new Message();
        message2.setContent("I reply to your message");
        message2.setType("sample");
        message2.setSender(userThree);
        message2.setReceiver(userTwo);
        message2.setReplyTo(message1);
        Date cat2 = Date.from(now.plus(1, ChronoUnit.MINUTES));
        message2.setCreatedAt(cat2);
        messageRepository.save(message2);

        Message message3 = new Message();
        message3.setContent("Hello john! how are you?");
        message3.setType("sample");
        message3.setSender(userOne);
        message3.setReceiver(userTwo);
        Date cat3 = Date.from(now.plus(15, ChronoUnit.MINUTES));
        message3.setCreatedAt(cat3);
        messageRepository.save(message3);

        // Box initialization
        Box box1 = new Box();
        box1.setName("Tle Hesse Team");
        boxRepository.save(box1);

        // Membership initialization
        MemberShip userOneMember = new MemberShip();
        userOneMember.setAdmin(false);
        userOneMember.setOwner(userOne);
        userOneMember.setBox(box1);
        memberShipRepository.save(userOneMember);

        MemberShip userTwoMember = new MemberShip();
        userTwoMember.setAdmin(true);
        userTwoMember.setOwner(userTwo);
        userTwoMember.setBox(box1);
        memberShipRepository.save(userTwoMember);

        MemberShip userThreeMember = new MemberShip();
        userThreeMember.setAdmin(false);
        userThreeMember.setOwner(userThree);
        userThreeMember.setBox(box1);
        memberShipRepository.save(userThreeMember);

        // message in box initialization
        Message inboxmess1 = new Message();
        inboxmess1.setContent("Hello everyone, and welcome in our team");
        inboxmess1.setType("inbox");
        inboxmess1.setSender(userTwo);
        inboxmess1.setBoxReceiver(box1);
        Date catim1 = Date.from(now.plus(2, ChronoUnit.MINUTES));
        inboxmess1.setCreatedAt(catim1);
        messageRepository.save(inboxmess1);

        Message inboxmess2 = new Message();
        inboxmess2.setContent("Thank you John for creating this team");
        inboxmess2.setType("inbox");
        inboxmess2.setSender(userOne);
        inboxmess2.setBoxReceiver(box1);
        Date catim2 = Date.from(now.plus(3, ChronoUnit.MINUTES));
        inboxmess2.setCreatedAt(catim2);
        messageRepository.save(inboxmess2);

        Message inboxmess3 = new Message();
        inboxmess3.setContent("Hello everyone, and welcome in our team");
        inboxmess3.setType("inbox");
        inboxmess3.setSender(userThree);
        inboxmess3.setBoxReceiver(box1);
        Date catim3 = Date.from(now.plus(4, ChronoUnit.MINUTES));
        inboxmess3.setCreatedAt(catim3);
        messageRepository.save(inboxmess3);

        return "Data initialized successfully";
    }

    @GetMapping("/messages")
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    @GetMapping("/boxes")
    public List<Box> getBoxes() {
        return boxRepository.findAll();
    }

    @GetMapping("/memberships")
    public List<MemberShip> getMemberShips() {
        return memberShipRepository.findAll();
    }

    @GetMapping("/friendships")
    public List<FriendShip> getFriendShips() {
        return friendShipRepository.findAll();
    }

    @GetMapping("/before_start/get_users")
    public List<DBUser> getUsers() {
        return userService.all();
    }

    @PostMapping("/login")
    public String getToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    @RequestMapping("/secret_page")
    public String testSecure() {
        return "Welcome to the secret page";
    }

    @GetMapping("/friends")
    public List<DBUser> testFriends(@AuthenticationPrincipal Jwt jwt) {
        DBUser subject = userService.get(jwt.getClaim("sub"));
        return userService.getFriends(subject);
    }
}
