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
    @Query("SELECT count(m) FROM Message m WHERE (m.sender = :subject_1 AND m.receiver = :subject_2)" +
            "OR (m.sender = :subject_2 AND m.receiver = :subject_1)")
    int count(DBUser subject_1, DBUser subject_2);

    @Query("SELECT m FROM Message m WHERE (m.sender = :subject_1 AND m.receiver = :subject_2)" +
            "OR (m.sender = :subject_2 AND m.receiver = :subject_1) ORDER BY m.createdAt DESC LIMIT 1")
    Message findFirst(DBUser subject_1, DBUser subject_2);

    @Query("SELECT m FROM Message m WHERE m.boxReceiver = :boxReceiver ORDER BY m.createdAt DESC LIMIT 1")
    Message findFirst(Box boxReceiver);

    @Query("SELECT m FROM Message m WHERE m.boxReceiver = :receiver ORDER BY m.createdAt DESC")
    List<Message> find(Box receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :sender AND m.receiver = :receiver) OR (m.sender = :receiver AND m.receiver = :sender)" +
            " ORDER BY m.createdAt DESC")
    List<Message> find(DBUser sender, DBUser receiver);

    @Query("SELECT m FROM Message m JOIN DBUser u ON m.sender = u WHERE m.receiver = :receiver AND m.isChecked = false")
    List<Message> findUnchecked(DBUser receiver);

    @Query("SELECT m FROM Message m WHERE m.sender = :sender AND m.receiver = :receiver AND m.isChecked = false")
    List<Message> findUnchecked(DBUser sender, DBUser receiver);

    @Query("SELECT m FROM Message m WHERE m.sender = :sender AND m.receiver = :receiver AND m.isSeen = false")
    List<Message> findUnseen(DBUser sender, DBUser receiver);

    @Query("SELECT m FROM Message m WHERE m.receiver = :receiver AND m.isSeen = false")
    List<Message> findUnseen(DBUser receiver);

    @Query("SELECT COUNT(u) FROM DBUser u RIGHT JOIN Message m ON (m.sender = u) WHERE m.receiver = :receiver AND m.isChecked = false")
    int countUnchecked(DBUser receiver);

    @Query("SELECT count(m) FROM Message m WHERE m.sender = :sender AND m.receiver = :receiver AND m.isSeen = false")
    int countUnseen(DBUser sender, DBUser receiver);

    @Query("SELECT count(m) FROM Message m WHERE m.receiver = :receiver AND m.isSeen = false")
    int countUnseen(DBUser receiver);

}
