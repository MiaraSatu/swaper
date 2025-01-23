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

    public FriendShip get(int id) {
        return friendShipRepository.findById(id).get();
    }

    public FriendShip get(DBUser user1, DBUser user2) {
        return friendShipRepository.findFirstBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }

    public void send(DBUser sender, String invitationText, DBUser receiver) {
        if(!checkFriendShip(sender, receiver) && sender.getId() != receiver.getId()) {
            FriendShip friendShip = new FriendShip();
            friendShip.setSender(sender);
            friendShip.setReceiver(receiver);
            friendShip.setCreatedAt(Date.from(Instant.now()));
            friendShip.setInvitationText(invitationText);
            friendShipRepository.save(friendShip);
        }
    }

    public void accept(DBUser subject, FriendShip friendShip) {
        if(friendShip.getReceiver().getId() == subject.getId()) {
            friendShip.setAccepted(true);
            friendShip.setUpdatedAy(Date.from(Instant.now()));
            friendShipRepository.save(friendShip);
        }
    }

    public void refuse(DBUser subject, FriendShip friendShip, String refusalText) {
        if(friendShip.getReceiver().getId() == subject.getId() && !friendShip.isAccepted()) {
            friendShip.setRefused(true);
            friendShip.setRefusalText(refusalText);
            friendShipRepository.save(friendShip);
        }
    }

    public void cancel(FriendShip friendShip) {
        if(!friendShip.isRefused() && !friendShip.isAccepted()) {
            friendShipRepository.delete(friendShip);
        }
    }

    public List<FriendShip> getFriendShipRelatedTo(DBUser subject) {
        return friendShipRepository.findBySenderOrReceiver(subject, subject);
    }

    public List<FriendShip> searchByUserName(String keyword, DBUser subject) {
        return friendShipRepository.searchByUserName(keyword, subject);
    }

    public Map<String, Object> getPaginedInvitation(boolean sent, DBUser subject, String baseUrl, Integer page, long limit) {
        List<FriendShip> invitations = sent
                ? friendShipRepository.findBySenderAndIsAccepted(subject, false)
                : friendShipRepository.findByReceiverAndIsAccepted(subject, false);
        invitations = invitations.stream().sorted((e1, e2) -> Long.compare(e2.getCreatedAt().getTime(), e1.getCreatedAt().getTime())).toList();
        return friendShipPaginator.paginate(invitations, baseUrl, page, limit);
    }

    public boolean checkFriendShip(DBUser user1, DBUser user2) {
        return null != get(user1, user2);
    }

}
