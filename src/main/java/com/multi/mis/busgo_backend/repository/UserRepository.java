package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    User findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    @Query("select u from User u where u.isActive = true ")
    List<User> findByIsActiveTrue();
} 