package com.example.RoyServices.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve cualquier archivo dentro de la carpeta "uploads" como /uploads/archivo.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
