package com.example.swaper.repository;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Integer> {
    public List<FriendShip> findBySenderOrReceiver(DBUser sender, DBUser receiver);

    @Query("SELECT f FROM FriendShip f JOIN DBUser u ON (f.sender = u AND f.receiver = :subject)"+
        "OR (f.receiver = u AND f.sender = :subject) WHERE u.name LIKE CONCAT('%', :kw, '%')"
    )
    public List<FriendShip> searchByUserName(@Param("kw") String kw, @Param("subject") DBUser subject);
}
