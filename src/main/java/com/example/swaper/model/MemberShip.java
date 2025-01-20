package com.example.swaper.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MemberShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean isAdmin;
    @ManyToOne
    private DBUser owner;
    @ManyToOne
    private Box box;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public DBUser getOwner() {
        return owner;
    }

    public void setOwner(DBUser owner) {
        this.owner = owner;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }
}
