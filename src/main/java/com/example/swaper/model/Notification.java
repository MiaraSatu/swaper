package com.example.swaper.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;
    @ManyToOne
    private DBUser intendedFor;
    @ManyToOne
    private FriendShip friendShip;
    @ManyToOne
    private MemberShip memberShip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DBUser getIntendedFor() {
        return intendedFor;
    }

    public void setIntendedFor(DBUser intendedFor) {
        this.intendedFor = intendedFor;
    }

    public FriendShip getFriendShip() {
        return friendShip;
    }

    public void setFriendShip(FriendShip friendShip) {
        this.friendShip = friendShip;
    }

    public MemberShip getMemberShip() {
        return memberShip;
    }

    public void setMemberShip(MemberShip memberShip) {
        this.memberShip = memberShip;
    }
}
