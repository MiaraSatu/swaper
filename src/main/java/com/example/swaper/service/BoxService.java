package com.example.swaper.service;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.MemberShip;
import com.example.swaper.repository.BoxRepository;
import com.example.swaper.repository.MemberShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private MemberShipRepository memberShipRepository;

    @Autowired
    private MemberShipService memberShipService;

    public Box get(int id) {
        return boxRepository.findById(id).get();
    }

    public List<Box> get(DBUser user) {
        List<MemberShip> memberShips = memberShipService.get(user);
        return memberShips.stream().map(MemberShip::getBox).toList();
    }

    public void add(Box box, DBUser admin) {
        boxRepository.save(box);
        MemberShip adminMemberShip = new MemberShip();
        adminMemberShip.setBox(box);
        adminMemberShip.setOwner(admin);
        adminMemberShip.setAdmin(true);
        memberShipRepository.save(adminMemberShip);
    }

    public void addUsers(Box box, List<DBUser> users) {
        for(DBUser user : users) {
            addUser(box, user);
        }
    }

    public void addUser(Box box, DBUser user) {
        // si pas encore membre
        if(!checkMember(box, user)) {
            MemberShip memberShip = new MemberShip();
            memberShip.setBox(box);
            memberShip.setOwner(user);
            memberShip.setAdmin(false);
            memberShipRepository.save(memberShip);
        }
    }

    public void removeUser(Box box, DBUser user) {
        // s'il est membre et n'est pas admin
        if(checkMember(box, user) && !checkMemberIsAdmin(box, user)) {
           MemberShip memberShip = memberShipRepository.findFirstByBoxAndOwner(box, user);
           memberShipRepository.delete(memberShip);
        }
    }

    public boolean checkMemberIsAdmin(Box box, DBUser member) {
        return null != memberShipRepository.findFirstByBoxAndOwnerAndIsAdmin(box, member,true);
    }

    public boolean checkMember(Box box, DBUser member) {
        return null != memberShipRepository.findFirstByBoxAndOwner(box, member);
    }
}
