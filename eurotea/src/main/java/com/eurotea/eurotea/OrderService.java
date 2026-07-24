package com.eurotea.eurotea;

import com.eurotea.eurotea.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // returns null if not found - order-success.html already checks for that
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}