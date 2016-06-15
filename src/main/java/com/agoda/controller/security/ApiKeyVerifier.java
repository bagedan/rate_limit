package com.agoda.controller.security;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Created by Tkachi on 6/15/2016.
 */
public interface ApiKeyVerifier extends HandlerInterceptor {

    public boolean verify(String apiKey);
}
