package com.example.swaper.repository;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Integer> {
    public List<FriendShip> findBySenderOrReceiver(DBUser sender, DBUser receiver);
}
