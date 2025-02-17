package com.example.swaper.repository;

import com.example.swaper.model.DBUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    public DBUser findByEmail(String email);

    @Query("SELECT u FROM DBUser u WHERE u != :subject AND u.name LIKE CONCAT('%',:kw,'%')")
    public List<DBUser> searchByName(@Param("kw") String kw, @Param("subject") DBUser subject);
}