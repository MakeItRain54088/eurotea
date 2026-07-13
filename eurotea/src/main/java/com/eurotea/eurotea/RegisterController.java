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
                                 @RequestParam("document") MultipartFile documentFile) {
        
        // 1. Create a brand new User object and fill in the details from the webpage
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setCompanyName(companyName);
        
        // Every new B2B registration starts as 'PENDING' because admin needs to review the file first
        newUser.setStatus("PENDING"); 

        // 2. Handle the file upload logic (PDF or image trade licenses)
        if (!documentFile.isEmpty()) {
            try {
                // FILE STORAGE PATH NOTE: 
                // Saving files inside a local "uploaded-docs" folder on the server.
                // Doing it this way because it is simple and works perfectly for testing and school demo.
                String uploadDirectory = System.getProperty("user.dir") + "/uploaded-docs/";
                
                // Create the folder automatically if it doesn't exist yet to prevent crashes
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Give the file a unique name using system time so files don't overwrite each other
                String originalFileName = documentFile.getOriginalFilename();
                String savedFileName = System.currentTimeMillis() + "_" + originalFileName;
                
                // Save the file on the computer hard drive
                File destinationFile = new File(uploadDirectory + savedFileName);
                documentFile.transferTo(destinationFile);

                // Save the file path string into the user object so we can read it later in MySQL
                newUser.setDocumentPath("/uploaded-docs/" + savedFileName);

            } catch (IOException e) {
                e.printStackTrace();
                // If file save fails, redirect back with a file error flag in the URL
                return "redirect:/register?fileError";
            }
        }

        // 3. Save our complete new user into MySQL database
        userRepository.save(newUser);

        // Registration done! Send them to login page and show a success notice
        return "redirect:/login?registered";
    }
}