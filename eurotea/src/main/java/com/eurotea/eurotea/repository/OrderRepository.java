package com.eurotea.eurotea.repository;

import com.eurotea.eurotea.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // not used yet - keeping this around for a future "my orders" page
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}