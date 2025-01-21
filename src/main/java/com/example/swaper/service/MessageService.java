package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    public long countMessageExchanged(DBUser user1, DBUser user2) {
        return messageRepository.countBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }
}
