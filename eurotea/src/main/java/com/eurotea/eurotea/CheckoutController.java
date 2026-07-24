package com.eurotea.eurotea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        double grandTotal = cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("grandTotal", grandTotal);

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("phone") String phone,
            @RequestParam("shippingAddress") String shippingAddress,
            HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        String companyName = (String) session.getAttribute("companyName");
        Long userId = (Long) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");

        Order order = new Order();
        order.setUserId(userId != null ? userId : 0L);
        order.setCustomerName(companyName != null ? companyName : "B2B Customer");
        order.setCustomerEmail(userEmail != null ? userEmail : "customer@example.com");
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);

        double grandTotal = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cart) {
            // convert to BigDecimal for the order record - money shouldn't stay a double
            BigDecimal itemPrice = BigDecimal.valueOf(item.getPriceUsed());
            OrderItem orderItem = new OrderItem(
                    item.getProductId(),
                    item.getProductName(),
                    itemPrice,
                    item.getQuantity()
            );
            orderItems.add(orderItem);
            grandTotal += item.getSubtotal();
        }

        order.setItems(orderItems);
        order.setTotalAmount(BigDecimal.valueOf(grandTotal));

        Order savedOrder = orderService.createOrder(order);
        // clear the cart only after the order actually saved
        session.removeAttribute("cart");

        return "redirect:/checkout/success/" + savedOrder.getId();
    }

    @GetMapping("/success/{orderId}")
    public String showSuccessPage(@PathVariable("orderId") Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order-success";
    }
}