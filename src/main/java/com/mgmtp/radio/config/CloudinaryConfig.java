package com.mgmtp.radio.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.api.key}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api.secret}")
    private String cloudinaryApiSecret;

    @Value("${cloudinary.api.cloud.name}")
    private String cloudinaryApiCloudName;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", cloudinaryApiCloudName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret);
        return new Cloudinary(config);
    }
}
