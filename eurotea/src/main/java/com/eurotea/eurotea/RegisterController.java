package com.eurotea.eurotea;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    // Show the B2B registration page html form
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // template/register.html
    }

    // Handle the registration form when user clicks the submit button
    @PostMapping("/register")
    public String handleRegister(@RequestParam("email") String email,
                                 @RequestParam("password") String password,
                                 @RequestParam("companyName") String companyName,
                                 @RequestParam(value = "document", required = false) MultipartFile documentFile,
                                 HttpSession session) {

        System.out.println("====== [DEBUG] Registration Attempt Received ======");
        System.out.println("Email: " + email + " | Company: " + companyName);

        // 1. Block reserving admin email as regular B2B account
        if ("admin@eurotea.com".equalsIgnoreCase(email)) {
            System.out.println("====== [DEBUG] Blocked attempt to register with admin email! ======");
            return "redirect:/register?error=adminEmail";
        }

        // 2. Create a brand new User object and fill in the details from the webpage
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setCompanyName(companyName);
        
        // Every new B2B registration starts as 'PENDING' because admin needs to review the file first
        newUser.setStatus("PENDING"); 

        // 3. Handle the file upload logic (PDF or image trade licenses)
        if (documentFile != null && !documentFile.isEmpty()) {
            try {
                String uploadDirectory = System.getProperty("user.dir") + "/uploaded-docs/";
                
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String originalFileName = documentFile.getOriginalFilename();
                String savedFileName = System.currentTimeMillis() + "_" + originalFileName;
                
                File destinationFile = new File(uploadDirectory + savedFileName);
                documentFile.transferTo(destinationFile);

                newUser.setDocumentPath("/uploaded-docs/" + savedFileName);
                System.out.println("====== [DEBUG] Document uploaded successfully: " + savedFileName);

            } catch (IOException e) {
                System.out.println("====== [DEBUG] Document upload failed! ======");
                e.printStackTrace();
                return "redirect:/register?fileError";
            }
        } else {
            System.out.println("====== [DEBUG] No document attached or file was empty.");
        }

        // 4. Save our complete new user into MySQL database
        User savedUser = userRepository.save(newUser);
        System.out.println("====== [DEBUG] SUCCESS! User saved with ID: " + savedUser.getId() + " ======");

        // 5. AUTO-LOGIN LOGIC:
        // Automatically save user profile into HTTP session so the user stays logged in immediately
        session.setAttribute("userRole", "B2B");
        session.setAttribute("userStatus", savedUser.getStatus()); // 'PENDING' status
        session.setAttribute("companyName", savedUser.getCompanyName());
        session.setAttribute("userId", savedUser.getId());

        // Redirect straight to the shop homepage instead of forcing them to log in again
        return "redirect:/shop";
    }
}