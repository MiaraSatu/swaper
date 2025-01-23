package com.example.swaper.service;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.Message;
import com.example.swaper.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PaginatorService<Message> messagePaginatorService;

    @Autowired
    @Lazy
    private DBUserService userService;

    @Autowired
    private MemberShipService memberShipService;

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private BoxService boxService;

    public Message get(int id) {
        return messageRepository.findById(id).get();
    }

    public long countMessageExchanged(DBUser user1, DBUser user2) {
        return messageRepository.countBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }

    public Map<String, Object> getPaginedDiscussions(DBUser subject, String baseUrl, Integer page, long limit) {
        List<DBUser> friends = userService.getFriends(subject);
        List<Message> messages = new ArrayList<>();
        // associer chaque friend à sa dernier message avec le subject
        for(DBUser friend: friends) {
            Message lastMessage = this.getLastMessageExchanged(subject, friend);
            if(lastMessage != null) messages.add(lastMessage);
        }
        // trier par order de la date de création les messages
        messages = messages.stream().sorted((e1, e2) -> Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime())).toList();
        return messagePaginatorService.paginate(messages, baseUrl,page, limit);
    }

    public Message getLastMessageExchanged(DBUser subject, DBUser friend) {
        return messageRepository.findFirstBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtDesc(subject, friend, friend, subject);
    }

    public boolean send(Message message, DBUser sender, int receiverId, String type, Integer replyToId) {
        if(type.equals("inbox")) {
            Box boxReceiver = boxService.get(receiverId);
            if(null == memberShipService.get(boxReceiver, sender)) return false;
            message.setBoxReceiver(boxReceiver);
            if(null != replyToId) {
                Message parent = this.get(replyToId);
                if(parent.getBoxReceiver().getId() == receiverId) message.setReplyTo(parent);
            }

        }
        else if(type.equals("sample")) {
            DBUser receiver = userService.get(receiverId);
            if(null == friendShipService.get(sender, receiver)) return false;
            message.setReceiver(receiver);
            if(null != replyToId) {
                Message parent = this.get(replyToId);
                if(checkMessageParticiper(parent, sender, receiver)) message.setReplyTo(parent);
            }
        }
        else {
            return false;
        }
        message.setType(type);
        message.setSender(sender);
        message.setCreatedAt(Date.from(Instant.now()));
        messageRepository.save(message);
        return true;
    }

    private boolean checkMessageParticiper(Message message, DBUser partant1, DBUser partant2) {
        return (message.getSender().getId() == partant1.getId() && message.getReceiver().getId() == partant2.getId())
                || (message.getSender().getId() == partant2.getId() && message.getReceiver().getId() == partant1.getId());
    }
}
