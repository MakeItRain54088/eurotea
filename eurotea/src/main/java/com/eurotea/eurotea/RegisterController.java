package com.eurotea.eurotea;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegisterController {

    // Only these document types are accepted, matching the "accept" attribute on the upload form.
    // Enforced here too since the HTML attribute alone can be bypassed (e.g. via curl/Postman).
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "png", "jpg", "jpeg");

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

            // Only trust the extension of the ORIGINAL filename for validation purposes;
            // the actual saved filename is always generated server-side (see below) so
            // nothing from the client ever reaches the filesystem path.
            String originalFileName = StringUtils.getFilename(documentFile.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(originalFileName);

            if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                System.out.println("====== [DEBUG] Rejected upload with disallowed file type: " + originalFileName);
                return "redirect:/register?fileError";
            }

            try {
                String uploadDirectory = System.getProperty("user.dir") + "/uploaded-docs/";

                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Generate a brand new random filename instead of reusing anything from the
                // client. This closes off path traversal (e.g. "../../evil.pdf") since the
                // client-supplied name never touches the destination path.
                String savedFileName = UUID.randomUUID() + "." + extension.toLowerCase();

                File destinationFile = new File(dir, savedFileName);
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
        session.setAttribute("userEmail", savedUser.getEmail());

        // Redirect straight to the shop homepage instead of forcing them to log in again
        return "redirect:/shop";
    }
}