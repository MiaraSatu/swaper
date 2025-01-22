package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PaginatorService<Message> messagePaginatorService;

    @Autowired
    @Lazy
    private DBUserService userService;

    public long countMessageExchanged(DBUser user1, DBUser user2) {
        return messageRepository.countBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }

    public List<Message> getDiscussions(DBUser subject, Integer page, long limit) {
        List<DBUser> friends = userService.getFriends(subject);
        List<Message> messages = new ArrayList<>();
        // associer chaque friend à sa dernier message avec le subject
        for(DBUser friend: friends) {
            Message lastMessage = this.getLastMessageExchanged(subject, friend);
            if(lastMessage != null) messages.add(lastMessage);
        }
        // trier par order de la date de création les messages
        messages = messages.stream().sorted((e1, e2) -> Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime())).toList();
        return messagePaginatorService.paginate(messages, page, limit);
    }

    public Message getLastMessageExchanged(DBUser subject, DBUser friend) {
        return messageRepository.findFirstBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtDesc(subject, friend, friend, subject);
    }
}
