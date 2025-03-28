package com.example.swaper.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class FriendShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean isAccepted = false;
    private String invitationText;
    private boolean isInvitationSeen = false;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    private DBUser sender;
    @ManyToOne
    private DBUser receiver;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getInvitationText() {
        return invitationText;
    }

    public void setInvitationText(String invitationText) {
        this.invitationText = invitationText;
    }

    public boolean isInvitationSeen() {
        return isInvitationSeen;
    }

    public void setInvitationSeen(boolean invitationSeen) {
        isInvitationSeen = invitationSeen;
    }

    public DBUser getSender() {
        return sender;
    }

    public void setSender(DBUser sender) {
        this.sender = sender;
    }

    public DBUser getReceiver() {
        return receiver;
    }

    public void setReceiver(DBUser receiver) {
        this.receiver = receiver;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
