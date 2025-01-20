package com.example.swaper.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String reaction;
    @ManyToOne
    private Message message;
    @ManyToOne
    private DBUser owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public DBUser getOwner() {
        return owner;
    }

    public void setOwner(DBUser owner) {
        this.owner = owner;
    }
}
