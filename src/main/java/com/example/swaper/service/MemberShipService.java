package com.example.swaper.service;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.MemberShip;
import com.example.swaper.repository.MemberShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberShipService {

    @Autowired
    private MemberShipRepository memberShipRepository;

    public List<MemberShip> getAll() {
        return memberShipRepository.findAll();
    }

    public MemberShip get(Box box, DBUser member) {
        return memberShipRepository.findFirstByBoxAndOwner(box, member);
    }
}
