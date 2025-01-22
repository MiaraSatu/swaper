package com.example.swaper.repository;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShipRepository extends JpaRepository<MemberShip, Integer> {
    public MemberShip findFirstByBoxAndOwnerAndIsAdmin(Box box, DBUser owner, boolean isAdmin);
    public MemberShip findFirstByBoxAndOwner(Box box, DBUser owner);
}