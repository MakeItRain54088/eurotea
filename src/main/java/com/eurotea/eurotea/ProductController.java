package com.eurotea.eurotea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 使用 @Controller 才能渲染 HTML 網頁
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/shop")
    public String viewShopPage(Model model) {
        // 從資料庫撈出所有茶葉商品，傳給前端網頁
        model.addAttribute("products", productRepository.findAll());
        return "shop"; 
    }
}