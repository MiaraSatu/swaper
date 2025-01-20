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
    private String type;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(nullable = false)
    private DBUser sender;
    @ManyToOne
    private DBUser receiver;
    @ManyToOne
    private Box boxReceiver;
    @ManyToOne
    private Message replyTo;

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
}
