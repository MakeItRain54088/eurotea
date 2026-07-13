package com.eurotea.eurotea;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives us all basic CRUD database functions like save(), findAll(), findById() automatically.
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query to find users by their review status.
    // Example: userRepository.findByStatus("PENDING") will give us all companies waiting for approval.
    List<User> findByStatus(String status);
}