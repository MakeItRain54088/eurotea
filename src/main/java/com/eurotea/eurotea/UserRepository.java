package com.eurotea.eurotea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // the JpaRepository interface provides CRUD operations for the User entity
}