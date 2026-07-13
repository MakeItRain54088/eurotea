package com.eurotea.eurotea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // controller to handle requests for the shop page
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/shop")
    public String viewShopPage(Model model) {
        // get all products from the repository and add them to the model
        model.addAttribute("products", productRepository.findAll());
        return "shop"; 
    }
}