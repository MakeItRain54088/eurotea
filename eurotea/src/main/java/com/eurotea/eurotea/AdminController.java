package com.eurotea.eurotea;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // Open the admin dashboard page
    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        
        // SECURITY CHECK: 
        // Check if the user is logged in and if they are an ADMIN.
        // If a normal user or hacker tries to type "/admin/dashboard" in the URL bar directly, 
        // they will be blocked and kicked out to the login page.
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/login?error"; // Block access and show error
        }

        // Find all B2B companies that are waiting for approval (Status must be PENDING)
        List<User> pendingUsers = userRepository.findByStatus("PENDING");
        
        // Send the list to the HTML template so we can display them in a table
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin-dashboard";
    }

    // When the admin clicks the "Approve" button
    @PostMapping("/admin/approve/{id}")
    public String approveUser(@PathVariable("id") Long id, HttpSession session) {
        
        // Double check security here too, just in case someone uses Postman to hack the API
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/login";
        }

        // Find the user by their ID number
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus("APPROVED"); // Change status from PENDING to APPROVED
            userRepository.save(user);  // Save the update back into MySQL database
        }
        
        // Go back to dashboard and show a green success message box
        return "redirect:/admin/dashboard?success";
    }
}