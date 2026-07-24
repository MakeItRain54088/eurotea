package com.eurotea.eurotea;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Exposes the uploaded-docs folder (outside the classpath, next to the running app)
    // at the "/uploaded-docs/**" URL so admins can open the trade license files linked
    // from the dashboard. Uploaded filenames are always server-generated (see
    // RegisterController), so this directory only ever contains safe, validated files.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:" + System.getProperty("user.dir") + "/uploaded-docs/";
        registry.addResourceHandler("/uploaded-docs/**")
                .addResourceLocations(uploadPath);
    }
}
