package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    @Lazy
    private DBUserService userService;

    public long countMessageExchanged(DBUser user1, DBUser user2) {
        return messageRepository.countBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }

    public List<Message> getDiscussions(DBUser subject) {
        List<DBUser> friends = userService.getFriends(subject);
        List<Message> messages = new ArrayList<>();
        for(DBUser friend: friends) {
            Message lastMessage = this.getLastMessageExchanged(subject, friend);
            messages.add(lastMessage);
        }
        return messages.stream().sorted((e1, e2) ->  Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime())).toList();
    }

    public Message getLastMessageExchanged(DBUser subject, DBUser friend) {
        return messageRepository.findFirstBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtDesc(subject, friend, friend, subject);
    }
}
