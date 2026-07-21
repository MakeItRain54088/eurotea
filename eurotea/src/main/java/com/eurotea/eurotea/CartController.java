package com.eurotea.eurotea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    // View shopping cart page
    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        List<CartItem> cart = getCartFromSession(session);
        
        // Calculate total order amount
        double grandTotal = cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("grandTotal", grandTotal);

        return "cart"; // templates/cart.html
    }

    // Add item to cart
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            List<CartItem> cart = getCartFromSession(session);

            // B2B DYNAMIC PRICING IN CART:
            // Check if user is APPROVED B2B. If yes, use wholesalePrice; otherwise, standard price.
            String userStatus = (String) session.getAttribute("userStatus");
            double finalPrice = "APPROVED".equals(userStatus) ? product.getWholesalePrice() : product.getPrice();

            // Check if product is already in the cart
            boolean exists = false;
            for (CartItem item : cart) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + 1);
                    exists = true;
                    break;
                }
            }

            // If new item, add to list
            if (!exists) {
                cart.add(new CartItem(product.getId(), product.getName(), finalPrice, 1));
            }

            // Update session
            session.setAttribute("cart", cart);
        }

        return "redirect:/shop";
    }

    // Clear cart or simulate checkout
    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        // Clear cart items from session after successful simulated payment
        session.removeAttribute("cart");
        return "redirect:/cart?success";
    }

    // Helper method to retrieve or initialize cart from session
    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}