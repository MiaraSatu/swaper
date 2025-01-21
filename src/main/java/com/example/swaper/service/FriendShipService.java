package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import com.example.swaper.repository.FriendShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendShipService {
    @Autowired
    private FriendShipRepository friendShipRepository;

    public List<FriendShip> getFriendShipRelatedTo(DBUser subject) {
        return friendShipRepository.findBySenderOrReceiver(subject, subject);
    }

    public List<FriendShip> searchByUserName(String keyword, DBUser subject) {
        return friendShipRepository.searchByUserName(keyword, subject);
    }

}
