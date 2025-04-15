package com.nofirst.ai.code.review.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "prompt")
public class PromptTemplatesConfiguration {

    private String system;

    private String user;

}
