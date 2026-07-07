package com.eurotea.eurotea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 繼承 JpaRepository 後，自動擁有內建的 findAll(), save(), delete() 功能
}