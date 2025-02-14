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
    @Query("SELECT f FROM FriendShip f JOIN DBUser u ON (f.sender = u AND f.receiver = :subject) OR (f.sender = :subject AND f.receiver = u)")
    public List<FriendShip> findAllByUser(@Param("subject") DBUser subject);

    @Query("SELECT f FROM FriendShip f JOIN DBUser u ON (f.sender = u AND f.receiver = :subject) OR (f.sender = :subject AND f.receiver = u) WHERE f.isAccepted = true")
    public List<FriendShip> findAcceptedByUser(@Param("subject") DBUser subject);

    public List<FriendShip> findByReceiverAndIsAccepted(DBUser receiver, boolean isAccepted);

    public List<FriendShip> findBySenderAndIsAccepted(DBUser sender, boolean isAccepted);

    public FriendShip findFirstBySenderAndReceiverOrSenderAndReceiver(DBUser sender1, DBUser receiver1, DBUser sender2, DBUser receiver2);

    @Query("SELECT f FROM FriendShip f JOIN DBUser u ON (f.sender = u AND f.receiver = :subject)"+
        "OR (f.receiver = u AND f.sender = :subject) WHERE u.name LIKE CONCAT('%', :kw, '%')"
    )
    public List<FriendShip> searchByUserName(@Param("kw") String kw, @Param("subject") DBUser subject);

    @Query("SELECT f FROM FriendShip f JOIN DBUser u ON (f.sender = u AND f.receiver = :subject)"+
        " OR (f.receiver = u AND f.sender = :subject) WHERE u.name LIKE CONCAT('%',:kw,'%') AND f.isAccepted = true"
    )
    public List<FriendShip> searchFriendByUserName(@Param("kw") String kw, @Param("subject") DBUser subject);
}
