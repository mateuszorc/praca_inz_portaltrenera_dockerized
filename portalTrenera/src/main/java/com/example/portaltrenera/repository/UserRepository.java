package com.example.portaltrenera.repository;

import com.example.portaltrenera.model.User;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.enabled = TRUE")
    Optional<User> findByEmailIfEnabled(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.enabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);

    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}
