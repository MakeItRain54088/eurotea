package com.eurotea.eurotea;

import org.springframework.data.jpa.repository.JpaRepository;

// Basic repo interface for our tea table. No advanced custom queries needed here yet.
public interface ProductRepository extends JpaRepository<Product, Long> {
}