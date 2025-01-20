package com.example.swaper.repository;

import com.example.swaper.model.DBUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    public DBUser findByEmail(String email);
}
