package com.eurotea.eurotea;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // Show the login page website
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // this goes to templates/login.html
    }

    // Process the login form submit button
    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              HttpSession session) {
        
        // ADMIN QUICK ACCESS: 
        // Just hardcoding the admin password here so it is easy to show the lecturer during the demo.
        // Later we should move this to database for better security.
        if (email.equals("admin@eurotea.com") && password.equals("admin123")) {
            session.setAttribute("userRole", "ADMIN");
            return "redirect:/admin/dashboard"; // Successful login -> go to admin panel
        }

        // B2B USER CHECK:
        // Get all users from the database and check if the email and password match
        // (yeah, this loads the whole table just to check one login - fine for now)
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                
                // Save user info into session so other pages know who is logged in
                session.setAttribute("userRole", "B2B");
                session.setAttribute("userStatus", u.getStatus());
                session.setAttribute("companyName", u.getCompanyName());
                session.setAttribute("userId", u.getId());
                session.setAttribute("userEmail", u.getEmail());
                
                return "redirect:/shop"; // Successful login -> go to the main shop page
            }
        }

        // If password or email is wrong, send them back to login page with an error mark in the URL
        return "redirect:/login?error";
    }

    // Logout function to clear everything
    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate(); // Destroy the session so the user is logged out completely
        return "redirect:/shop"; // go back to the homepage
    }
}