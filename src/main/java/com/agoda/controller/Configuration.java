package com.agoda.controller;

import com.agoda.controller.db.DataProvider;
import com.agoda.controller.db.DataProviderImpl;
import com.agoda.controller.security.ApiKeyRateLimitVerifier;
import com.agoda.controller.security.ApiKeyVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

/**
 * Created by Tkachi on 6/15/2016.
 */
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:${env}.properties")
@EnableWebMvc
public class Configuration extends WebMvcConfigurerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    @Autowired
    private Environment env;

    @Bean
    public DataProvider dataProvider() throws IOException {
        String sourceLocation = env.getProperty("hotel.csv.location");
        return new DataProviderImpl(sourceLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(buildApiKeyVerifier());
    }

    private ApiKeyVerifier buildApiKeyVerifier() {
        ApiKeyRateLimitVerifier apiKeyVerifier = new ApiKeyRateLimitVerifier();
        String availableApiKey = env.getProperty("available.api.keys");
        LOGGER.info("Available apiKeys: [{}]", availableApiKey);
        for (String apiKey : availableApiKey.split(",")) {
            String customLimit = env.getProperty(apiKey + ".limit.seconds");
            if (customLimit != null) {
                LOGGER.debug("For apiKay [{}] custom limit is [{}] seconds", apiKey, customLimit);
                apiKeyVerifier.addApiKeyWithLimit(apiKey, Long.parseLong(customLimit));
            } else {
                LOGGER.debug("For apiKay [{}] using default limit", apiKey);
                apiKeyVerifier.addApiKey(apiKey);
            }
        }
        return apiKeyVerifier;
    }
}
