package com.example.swaper.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String content;
    private String type;
    private Date createdAt;
    private Date updatedAt;
    private boolean isSeen = false;
    private boolean isChecked = false;
    @ManyToOne
    @JoinColumn(nullable = false)
    private DBUser sender;
    @ManyToOne
    private DBUser receiver;
    @ManyToOne
    private Box boxReceiver;
    @ManyToOne
    private Message replyTo;
    // only needs in discussion list
    @Transient
    private int uncheckedCount = 0;
    @Transient
    private int unreadCount = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public Box getBoxReceiver() {
        return boxReceiver;
    }

    public void setBoxReceiver(Box boxReceiver) {
        this.boxReceiver = boxReceiver;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    public int getUncheckedCount() {
        return uncheckedCount;
    }

    public void setUncheckedCount(int uncheckedCount) {
        this.uncheckedCount = uncheckedCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
