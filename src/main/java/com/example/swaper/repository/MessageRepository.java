package com.example.swaper.repository;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    long countBySenderAndReceiverOrSenderAndReceiver(DBUser user1, DBUser user2 , DBUser user3, DBUser user4); // user3 = user1 && user4 = user2
    Message findFirstBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtDesc(DBUser user1, DBUser user2, DBUser user3, DBUser user4);
    List<Message> findBySenderAndBoxReceiverOrderByCreatedAtDesc(DBUser sender, Box receiver);
    List<Message> findBySenderAndReceiverAndIsChecked(DBUser sender, DBUser receiver, boolean isChecked);
    int countBySenderAndReceiverAndIsSeen(DBUser sender, DBUser receiver, boolean isSeen);
    List<Message> findBySenderAndReceiverAndIsSeen(DBUser sender, DBUser receiver, boolean isSeen);

    @Query("SELECT m FROM Message m WHERE (m.sender = :sender AND m.receiver = :receiver) OR (m.sender = :receiver AND m.receiver = :sender)" +
            " ORDER BY m.createdAt DESC")
    List<Message> findBySenderAndReceiverOrderByCreatedAtDesc(@Param("sender") DBUser sender, @Param("receiver") DBUser Receiver);
}
