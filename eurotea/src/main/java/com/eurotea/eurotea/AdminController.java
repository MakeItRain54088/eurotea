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
            System.out.println("====== [DEBUG ADMIN] Kicked out: Not logged in as ADMIN ======");
            return "redirect:/login?error"; // Block access and show error
        }

        // Find all B2B companies that are waiting for approval (Status must be PENDING)
        List<User> pendingUsers = userRepository.findByStatus("PENDING");
        
        // DEBUG PRINT IN CONSOLE (Check your Terminal!)
        System.out.println("====== [DEBUG ADMIN] Dashboard Loaded ======");
        System.out.println("Pending users found: " + (pendingUsers != null ? pendingUsers.size() : 0));
        
        // If findByStatus returns empty, fallback to fetch all users just in case
        // (so the table isn't just blank once everyone's already been approved/rejected)
        if (pendingUsers == null || pendingUsers.isEmpty()) {
            List<User> allUsers = userRepository.findAll();
            System.out.println("Fallback total users in DB: " + allUsers.size());
            model.addAttribute("pendingUsers", allUsers);
            model.addAttribute("users", allUsers); // Fix name mismatch for Thymeleaf template
        } else {
            // Send the list to the HTML template so we can display them in a table
            model.addAttribute("pendingUsers", pendingUsers);
            model.addAttribute("users", pendingUsers); // Both key names supported for HTML
        }

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
            System.out.println("====== [DEBUG ADMIN] Approved User ID: " + id + " ======");
        }
        
        // Go back to dashboard and show a green success message box
        return "redirect:/admin/dashboard?approved";
    }

    // When the admin clicks the "Reject" button
    @PostMapping("/admin/reject/{id}")
    public String rejectUser(@PathVariable("id") Long id, HttpSession session) {
        
        // Double check security here too
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/login";
        }

        // Find the user by their ID number
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus("REJECTED"); // Change status from PENDING to REJECTED
            userRepository.save(user);  // Save the update back into MySQL database
            System.out.println("====== [DEBUG ADMIN] Rejected User ID: " + id + " ======");
        }
        
        // Go back to dashboard and show a red alert message
        return "redirect:/admin/dashboard?rejected";
    }
}