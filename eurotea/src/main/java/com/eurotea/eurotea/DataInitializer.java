package com.eurotea.eurotea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // MOCK DATA SYSTEM:
        // If the product table inside MySQL is empty, add some default tea products automatically.
        // This is very helpful for the project demo because we don't have to add products manually every time.
        if (productRepository.count() == 0) {
            
            // Product 1
            Product p1 = new Product();
            p1.setName("Alishan High-Mountain Oolong");
            p1.setDescription("Hand-picked premium loose leaf tea from Taiwan's high altitude farms. Crisp floral notes.");
            p1.setPrice(35.00);          // Regular customer price
            p1.setWholesalePrice(19.50); // Cheaper wholesale price for approved B2B companies
            p1.setStockLevel(50);        // 50 kg available
            productRepository.save(p1);

            // Product 2
            Product p2 = new Product();
            p2.setName("Sun Moon Lake Ruby Black Tea");
            p2.setDescription("Rare Taiwanese black tea (TTES No.18) boasting natural hints of cinnamon and mint.");
            p2.setPrice(42.00);
            p2.setWholesalePrice(24.00);
            p2.setStockLevel(40);
            productRepository.save(p2);

            // Product 3
            Product p3 = new Product();
            p3.setName("Oriental Beauty (Dongfang Meiren)");
            p3.setDescription("Heavy-oxidized oolong bitten by tea green leafhoppers, creating a distinct honey-like sweetness.");
            p3.setPrice(55.00);
            p3.setWholesalePrice(32.50);
            p3.setStockLevel(15);
            productRepository.save(p3);

            System.out.println("====== Setup finished: 3 default tea products added to the database! ======");
        }
    }
}