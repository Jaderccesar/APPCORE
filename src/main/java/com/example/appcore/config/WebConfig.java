package com.example.appcore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String ABSOLUTE_UPLOAD_PATH = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars" + File.separator;

    private static final String UPLOAD_DIR = "file:" + ABSOLUTE_UPLOAD_PATH;

    private static final String RESOURCE_HANDLER = "/avatars/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(RESOURCE_HANDLER)
                .addResourceLocations(UPLOAD_DIR);
    }
}
