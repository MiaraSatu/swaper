package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import com.example.swaper.repository.FriendShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FriendShipService {
    @Autowired
    private FriendShipRepository friendShipRepository;

    @Autowired
    private PaginatorService<FriendShip> friendShipPaginator;

    @Autowired
    private DBUserService userService;

    public FriendShip get(int id) {
        return friendShipRepository.findById(id).get();
    }

    public FriendShip get(DBUser user1, DBUser user2) {
        return friendShipRepository.findFirstBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }

    public FriendShip send(DBUser sender, String invitationText, DBUser receiver) {
        if(!checkFriendShip(sender, receiver) && sender.getId() != receiver.getId()) {
            FriendShip friendShip = new FriendShip();
            friendShip.setSender(sender);
            friendShip.setReceiver(receiver);
            friendShip.setCreatedAt(Date.from(Instant.now()));
            friendShip.setUpdatedAt(Date.from(Instant.now()));
            friendShip.setInvitationText(invitationText);
            // complete information
            receiver.setFriendStatus("sent");
            friendShipRepository.save(friendShip);
            return friendShip;
        }
        return null;
    }

    public FriendShip accept(DBUser subject, FriendShip friendShip) {
        if(friendShip.getReceiver().getId() == subject.getId()) {
            friendShip.setAccepted(true);
            friendShip.setUpdatedAt(Date.from(Instant.now()));
            friendShipRepository.save(friendShip);
            // complete sender information
            DBUser sender = friendShip.getSender();
            sender.setFriendStatus("friend");
            return friendShip;
        }
        return null;
    }

    public DBUser refuse(DBUser subject, FriendShip friendShip) {
        if(friendShip.getReceiver().getId() == subject.getId() && !friendShip.isAccepted()) {
            DBUser sender = friendShip.getSender();
            friendShipRepository.delete(friendShip);
            // complete sender information
            sender.setFriendStatus("none");
            return sender;
        }
        return null;
    }

    public DBUser cancel(FriendShip friendShip) {
        if(!friendShip.isAccepted()) {
            DBUser receiver = friendShip.getReceiver();
            // complete receiver information
            receiver.setFriendStatus("none");
            friendShipRepository.delete(friendShip);
            return receiver;
        }
        return null;
    }

    public List<FriendShip> getFriendShipRelatedTo(DBUser subject) {
        return friendShipRepository.findAllByUser(subject);
    }

    public List<FriendShip> getAcceptedFriendShipRelatedTo(DBUser subject) {
        return friendShipRepository.findAcceptedByUser(subject);
    }

    public List<FriendShip> searchByUserName(String keyword, DBUser subject) {
        return friendShipRepository.searchByUserName(keyword, subject);
    }

    public Map<String, Object> getPaginedInvitation(boolean sent, DBUser subject, String baseUrl, Integer page, long limit) {
        List<FriendShip> invitations = sent
                ? friendShipRepository.findBySenderAndIsAccepted(subject, false)
                : friendShipRepository.findByReceiverAndIsAccepted(subject, false);
        invitations = invitations
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime()))
                .peek(inv -> this.complete(inv, subject))
                .toList();
        return friendShipPaginator.paginate(invitations, baseUrl, page, limit);
    }

    public boolean checkFriendShip(DBUser user1, DBUser user2) {
        return null != get(user1, user2);
    }

    public void complete(FriendShip friendShip, DBUser currentUser) {
        DBUser sender = friendShip.getSender(), receiver = friendShip.getReceiver();
        userService.complete(sender, currentUser);
        userService.complete(receiver, currentUser);
    }

}
