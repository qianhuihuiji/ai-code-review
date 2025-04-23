package com.nofirst.ai.code.review.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gitlab")
public class GitlabConfiguration {

    private String url;

    private String token;

}
