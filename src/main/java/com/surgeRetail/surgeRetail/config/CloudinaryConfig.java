package com.surgeRetail.surgeRetail.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    String cloudinaryName;

    @Value("${cloudinary.api-key}")
    String apiKey;

    @Value("${cloudinary.api-secret}")
    String apiSecret;

    @Bean
    public Cloudinary Cloudinary(){
        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudinaryName,
                "api_key",apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }
}
