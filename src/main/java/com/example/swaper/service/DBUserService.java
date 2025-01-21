package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import com.example.swaper.repository.DBUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DBUserService {
    @Autowired
    private DBUserRepository userRepo;

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private MessageService messageService;

    public void add(DBUser dbUser) {
        userRepo.save(dbUser);
    }

    public List<DBUser> all() {
        return userRepo.findAll();
    }

    public DBUser get(String email) {
        return userRepo.findByEmail(email);
    }

    public DBUser get(int id) {
        return userRepo.findById(id).get();
    }

    public List<DBUser> getMostFrequentedFriends(DBUser subject) {
        List<DBUser> friends = this.getFriends(subject);
        List<DBUser> frequentedFriends;
        Map<DBUser, Long> messageStat = new HashMap<>();
        for(DBUser friend: friends) {
            messageStat.put(friend, messageService.countMessageExchanged(subject, friend));
        }
        frequentedFriends = messageStat.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .limit(10)
                .toList();
        return frequentedFriends;
    }

    public List<DBUser> getFriends(DBUser subject) {
        List<FriendShip> friendShips = friendShipService.getFriendShipRelatedTo(subject);
        List<DBUser> friends = new ArrayList<>();
        for (FriendShip friendShip : friendShips) {
            DBUser friend = friendShip.getSender() == subject ? friendShip.getReceiver() : friendShip.getSender();
            if(!friends.contains(friend)) {
                friends.add(friend);
            }
        }
        return friends;
    }

}
