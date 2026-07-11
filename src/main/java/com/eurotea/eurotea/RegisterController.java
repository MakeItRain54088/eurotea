package com.eurotea.eurotea;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    // 1. show the registration page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // 指向 templates/register.html
    }

    // 2. form submission handler
    @PostMapping("/register")
    public String handleRegistration(@RequestParam("companyName") String companyName,
                                     @RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("pdfFile") MultipartFile file) {
        
        String savedPath = "";

        // check if the uploaded file is not empty
        if (!file.isEmpty()) {
            try {
                // put the uploaded file into a specific folder
                String uploadDir = "C:/eurotea/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs(); // if the directory does not exist, create it
                }

                // combine current timestamp with the original filename to avoid overwriting
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File destFile = new File(uploadDir + fileName);
                
                // put the uploaded file into the destination file
                file.transferTo(destFile);
                savedPath = destFile.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/register?error";
            }
        }

        // create a new user with "PENDING" status
        User newUser = new User(companyName, email, password, savedPath, "PENDING");
        userRepository.save(newUser);

        // turn the user to a success page or back to the registration page with a success message
        return "redirect:/register?success";
    }
}