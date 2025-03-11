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

    public int countMessageExchanged(DBUser user1, DBUser user2) {
        return messageRepository.count(user1, user2);
    }

    public Map<String, Object> getPaginedDiscussions(DBUser subject, String baseUrl, Integer page, long limit) {
        List<DBUser> friends = userService.getFriends(subject);
        List<Message> messages = new ArrayList<>();
        // associer chaque friend à sa dernier message avec le subject
        for(DBUser friend: friends) {
            Message lastMessage = this.getLastMessageExchanged(subject, friend);
            if(lastMessage != null) messages.add(lastMessage);
        }

        // boxes discussions
        List<Box> boxes = boxService.get(subject);
        for(Box box: boxes) {
            Message lastMessage = messageRepository.findFirst(box);
            if(lastMessage != null) messages.add(lastMessage);
        }
        // trier par order de la date de création les messages
        messages = messages.stream().sorted((e1, e2) -> Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime())).toList();
        Map<String, Object> paginedData = messagePaginatorService.paginate(messages, baseUrl,page, limit);
        List<Message> paginedMessages = (List<Message>)paginedData.get("data");
        paginedData.put("data", paginedMessages.stream().map(message -> {
            if(message.getSender().getId() == subject.getId()) return message;
            // List<Message> unchecked = messageRepository.getUncheckedBySender(message.getSender(), subject);
            List<Message> unchecked = messageRepository.findUnchecked(message.getSender(), subject);
            // check messages
            if(!unchecked.isEmpty()) {
                message.setUncheckedCount(unchecked.size());
                unchecked.forEach(ucMessage -> {
                    ucMessage.setChecked(true);
                    messageRepository.save(ucMessage);
                });
            }
            // count unread messages
            message.setUnreadCount(messageRepository.countUnseen(message.getSender(), subject));
            return message;
        }).toList());
        return paginedData;
    }

    public Message getLastMessageExchanged(DBUser subject, DBUser friend) {
        return messageRepository.findFirst(subject, friend);
        // return messageRepository.findFirstBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtDesc(subject, friend, friend, subject);
    }

    public Map<String, Object> getPaginedMessagesExchanged(DBUser subject, String type, int receiverId, String baseUrl, Integer page, long limit) {
        if(type.equals("sample")) {
            DBUser friend = userService.get(receiverId);
            List<Message> messages = messageRepository.find(subject, friend);
            Map<String, Object> paginedData = messagePaginatorService.paginate(messages, baseUrl, page, limit);
            List<Message> paginedMessages = (List<Message>)(paginedData.get("data"));
            paginedData.put("data", paginedMessages.stream().peek(pMessage -> {
                if(pMessage.getSender().getId() != subject.getId()) {
                    List<Message> unseen = messageRepository.findUnseen(pMessage.getSender(), subject);
                    unseen.forEach(usMessage -> {
                        usMessage.setSeen(true);
                        messageRepository.save(usMessage);
                    });
                }
            }).toList());
            return paginedData;
        } else if(type.equals("inBox")) {
            Box box = boxService.get(receiverId);
            List<Message> messages = messageRepository.find(box);
            return messagePaginatorService.paginate(messages, baseUrl, page, limit);
        }
        return null;
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

    public List<Message> getUnreadDiscussion(DBUser subject) {
        return messageRepository.findUnseen(subject);
    }

    public int countUnreadDiscussion(DBUser subject) {
        return messageRepository.countUnseen(subject);
    }

    public int countUnreadDiscussion(DBUser receiver, DBUser sender) {
        return messageRepository.countUnseen(sender, receiver);
    }

    public List<Message> getUncheckedDiscussion(DBUser subject) {
        return messageRepository.findUnchecked(subject);
    }

    public int countUncheckedDiscussion(DBUser subject) {
        return messageRepository.countUnchecked(subject);
    }

    private boolean checkMessageParticiper(Message message, DBUser partant1, DBUser partant2) {
        return (message.getSender().getId() == partant1.getId() && message.getReceiver().getId() == partant2.getId())
                || (message.getSender().getId() == partant2.getId() && message.getReceiver().getId() == partant1.getId());
    }
}
