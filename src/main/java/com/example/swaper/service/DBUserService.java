package com.example.swaper.service;

import com.example.swaper.model.Box;
import com.example.swaper.model.DBUser;
import com.example.swaper.model.FriendShip;
import com.example.swaper.repository.DBUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DBUserService {
    @Autowired
    private DBUserRepository userRepo;

    @Autowired
    @Lazy
    private FriendShipService friendShipService;

    @Autowired
    private PaginatorService<DBUser> paginatorService;

    @Autowired
    @Lazy
    private MessageService messageService;

    public void add(DBUser dbUser) {
        userRepo.save(dbUser);
    }

    public List<DBUser> all() {
        return userRepo.findAll();
    }

    public DBUser get(String email) {
        return userRepo.findByEmail(email);
    }

    public DBUser get(int id) {
        return userRepo.findById(id).get();
    }

    public boolean update(DBUser edited) {
        if(edited.getName().isBlank() || edited.getEmail().isBlank()) {
            return false;
        }
        userRepo.save(edited);
        return true;
    }

    public List<DBUser> getMostFrequentedFriends(DBUser subject) {
        List<DBUser> friends = this.getFriends(subject);
        List<DBUser> frequentedFriends;
        Map<DBUser, Long> messageStat = new HashMap<>();
        for(DBUser friend: friends) {
            messageStat.put(friend, messageService.countMessageExchanged(subject, friend));
        }
        frequentedFriends = messageStat.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(item -> {
                    this.complete(item.getKey(), subject);
                    return item.getKey();
                })
                .limit(10)
                .toList();
        return frequentedFriends;
    }

    public List<DBUser> getFriends(DBUser subject) {
        List<FriendShip> friendShips = friendShipService.getAcceptedFriendShipRelatedTo(subject)
            .stream()
            .sorted((e1, e2) -> Long.compare(e2.getUpdatedAt().getTime() ,e1.getUpdatedAt().getTime()))
            .peek(fs -> friendShipService.complete(fs, subject))
            .toList();
        List<DBUser> friends = new ArrayList<>();
        for (FriendShip friendShip : friendShips) {
            DBUser friend = friendShip.getSender() == subject ? friendShip.getReceiver() : friendShip.getSender();
            if(!friends.contains(friend)) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public List<DBUser> getSuggestions(DBUser subject) {
        List<DBUser> related = friendShipService.getFriendShipRelatedTo(subject)
                .stream()
                .map(friendShip -> {
                    if(friendShip.getSender().getId() == subject.getId()) return friendShip.getReceiver();
                    return friendShip.getSender();
                })
                .toList();
        List<DBUser> suggestions = all();
        suggestions = suggestions
                .stream()
                .filter(user -> !related.contains(user) && user != subject)
                .peek(nf -> this.complete(nf, subject))
                .toList();
        return suggestions;
    }

    public Map<String, Object> getPaginedFriends(DBUser subject, String baseUrl, Integer page, long limit) {
        List<DBUser> friends = this.getFriends(subject);
        return paginatorService.paginate(friends, baseUrl, page, limit);
    }

    public Map<String, Object> getPaginedSuggestions(DBUser subject, String baseUrl, Integer page, long limit) {
        List<DBUser> notFriends = this.getSuggestions(subject);
        return paginatorService.paginate(notFriends, baseUrl, page, limit);
    }

    public List<DBUser> searchDiscusser(String kw, DBUser subject) {
        return friendShipService.searchFriendByUserName(kw, subject)
            .stream().map(friendShip -> {
                DBUser sender = friendShip.getSender();
                if(sender.getId() == subject.getId())
                    return friendShip.getReceiver();
                return sender;
            }).toList();
    }

    public List<DBUser> search(String kw, DBUser subject) {
        List<DBUser> results = userRepo.searchByName(kw, subject);
        this.complete(results, subject);
        return results;
    }

    public List<DBUser> complete(List<DBUser> users, DBUser currentUser) {
        return users.stream().map(u -> {
            this.complete(u, currentUser);
            return u;
        }).toList();
    }

    public void complete(DBUser user, DBUser currentUser) {
        FriendShip friendShip = friendShipService.get(user, currentUser);
        if(user.getId() == currentUser.getId()) {
            user.setFriendStatus("you");
            return;
        }
        if(null != friendShip) {
            if(friendShip.isAccepted())
                user.setFriendStatus("friend");
            else {
                if(friendShip.getSender().getId() == currentUser.getId())
                    user.setFriendStatus("sent");
                else
                    user.setFriendStatus("received");
            }
        } else {
            user.setFriendStatus("none");
        }
    }

    public List<DBUser> getBoxMembers(Box box) {
        return userRepo.findByBox(box);
    }

}
