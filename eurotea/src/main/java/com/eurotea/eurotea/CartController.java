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

        return "redirect:/cart";
    }

    // Update the quantity of an existing cart line
    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam("productId") Long productId,
                                  @RequestParam("quantity") int quantity,
                                  HttpSession session) {
        List<CartItem> cart = getCartFromSession(session);

        // find the matching line first
        CartItem itemToUpdate = null;
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                itemToUpdate = item;
                break;
            }
        }

        if (itemToUpdate != null) {
            // treat 0 (or negative, in case someone messes with the form) as a remove
            if (quantity <= 0) {
                cart.remove(itemToUpdate);
            } else {
                itemToUpdate.setQuantity(quantity);
            }
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Remove a single item from the cart entirely
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("productId") Long productId, HttpSession session) {
        List<CartItem> cart = getCartFromSession(session);

        CartItem itemToRemove = null;
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            cart.remove(itemToRemove);
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Helper method to retrieve or initialize cart from session
    // (no cart table in the DB, it just lives in the session as a plain list)
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