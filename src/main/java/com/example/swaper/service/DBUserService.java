package com.example.swaper.service;

import com.example.swaper.model.DBUser;
import com.example.swaper.repository.DBUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBUserService {
    @Autowired
    private DBUserRepository userRepo;

    public void add(DBUser dbUser) {
        userRepo.save(dbUser);
    }

    public List<DBUser> all() {
        return userRepo.findAll();
    }
}
