package com.eurotea.eurotea;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            // 如果資料庫是空的，就自動塞入規格書中寫好的 3 款台灣茶
            if (repository.count() == 0) {
                repository.save(new Product("Taiwan Lemon Tea", 15.00, 50));
                repository.save(new Product("Classic Black Tea", 12.00, 100));
                repository.save(new Product("Oolong Tea", 18.00, 30));
                System.out.println("★ EuroTea Product initialize successfully！");
            }
        };
    }
}